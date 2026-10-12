package com.zzsd.inolibi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.common.RedissonRateLimiter;
import com.zzsd.inolibi.constants.RabbitMQConstant;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.entity.chart.dto.QueryChartDto;
import com.zzsd.inolibi.entity.chart.vo.ChartVo;
import com.zzsd.inolibi.entity.enums.ChartStatusEnum;
import com.zzsd.inolibi.entity.page.PageResult;
import com.zzsd.inolibi.mapper.ChartMapper;
import com.zzsd.inolibi.service.IAIService;
import com.zzsd.inolibi.service.IChartService;
import com.zzsd.inolibi.utils.AnalysisResult;
import com.zzsd.inolibi.utils.CurrentHolder;
import com.zzsd.inolibi.utils.ExcelUtils;
import com.zzsd.inolibi.utils.LlmResponseParser;
import jakarta.annotation.Resource;
import jodd.util.ArraysUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 图表信息表 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Slf4j
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

    @Resource
    private IAIService aiService;

    @Resource
    private RedissonRateLimiter redissonRateLimiter;

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * 根据ai分析得到图表数据
     *
     * @param chartByAIDto  生成图表dto
     * @param multipartFile 上传的文件
     * @return 分析结果
     */
    @Override
    public Chart getChartDataByAI(ChartByAIDto chartByAIDto, MultipartFile multipartFile) {
        // 限流
        Long userId = CurrentHolder.getCurrentId();
        redissonRateLimiter.rateLimiter("getChartDataByAI_" + userId);

        // 判断文件是否合法
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (!ObjectUtil.contains(Arrays.asList("xlsx", "xls"), suffix)) {
            throw new RuntimeException("文件格式不合法");
        }

        // 1. 读取文件内容
        String csvStr = ExcelUtils.excelToCsv(multipartFile);
        // 2. 调用ai接口
        String goal = chartByAIDto.getGoal();
        String chartName = chartByAIDto.getChartName();
        String chartType = chartByAIDto.getChartType();
        String conversationId = UUID.randomUUID() + "-" + userId;
        // 构建问题
        String question = """
                分析目标:%s
                图表类型:%s
                图表名称:%s
                文件数据:%s
                """.formatted(chartType, chartName, goal, csvStr);
        // 调用大模型
        String modelResponse = aiService.getAIAnswer(question, conversationId);

        AnalysisResult result = LlmResponseParser.parse(modelResponse);
        // 分析结论
        String conclusion = result.conclusion();
        // echarts代码
        String echartsCode = result.echartsCode();
        // 3. 保存文档内容到mysql
        Chart chart = Chart.builder()
                .chartType(chartType).chartName(chartName).goal(goal)
                .chartData(csvStr) // 原始数据
                .genChart(echartsCode) // echarts代码
                .genResult(conclusion) // 分析结论
                .userId(userId).build();
        this.save(chart);
        // 3. 返回结果
        return chart;
    }

    /**
     * 条件查询图表
     *
     * @param queryChartDto 查询图表信息dto
     * @return PageResult<ChartVo> 分页列表
     */
    @Override
    public PageResult<ChartVo> listCharts(QueryChartDto queryChartDto) {
        // 1. 构建分页查询对象
        Page<Chart> page = new Page<>(queryChartDto.getPageNum(), queryChartDto.getPageSize());

        String chartName = queryChartDto.getChartName();
        // 2. 构建查询条件
        LambdaQueryWrapper<Chart> lambdaQueryWrapper = Wrappers.<Chart>lambdaQuery()
                .eq(Chart::getUserId, CurrentHolder.getCurrentId())
                .like(ObjectUtil.isNotEmpty(chartName), Chart::getChartName, chartName)
                .eq(ObjectUtil.isNotEmpty(queryChartDto.getChartType()), Chart::getChartType, queryChartDto.getChartType())
                .orderByDesc(Chart::getCreateTime);

        // 3. 查询列表并返回
        Page<Chart> chartPage = page(page, lambdaQueryWrapper);
        return PageResult.<ChartVo>builder()
                .total(chartPage.getTotal())
                .records(BeanUtil.copyToList(chartPage.getRecords(), ChartVo.class))
                .build();
    }

    /**
     * 让ai异步分析得到结果
     *
     * @param chartByAIDto  生成图表dto
     * @param multipartFile 上传的文件
     * @return Chart
     */
    @Override
    public Chart getChartDataByAIAsync(ChartByAIDto chartByAIDto, MultipartFile multipartFile) {
        // 限流
        Long userId = CurrentHolder.getCurrentId();
        redissonRateLimiter.rateLimiter("getChartDataByAIAsync" + userId);

        // 判断文件是否合法
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (!ObjectUtil.contains(Arrays.asList("xlsx", "xls"), suffix)) {
            throw new RuntimeException("文件格式不合法");
        }

        // 1. 读取文件内容
        String csvStr = ExcelUtils.excelToCsv(multipartFile);

        String chartName = chartByAIDto.getChartName();
        String goal = chartByAIDto.getGoal();
        String chartType = chartByAIDto.getChartType();

        // 创建实例保存数据库
        Chart chart = Chart.builder()
                .chartType(chartType).chartName(chartName).goal(goal)
                .chartData(csvStr) // 原始数据
                .userId(userId).status(ChartStatusEnum.PENDING)
                .build();

        // 将实例保存到数据库当中进行返回
        save(chart);
        Long chartId = chart.getId();

        // 将消息发送到消息队列当中
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstant.CHART_QUEUE, chartId);
        } catch (Exception e) {
            log.error("发送 MQ 失败, chartId={}", chartId, e);
            // 兜底：标记失败，或记录到本地表稍后重发
            try {
                update(Wrappers.<Chart>lambdaUpdate()
                        .eq(Chart::getId, chartId)
                        .set(Chart::getStatus, ChartStatusEnum.FAILED)
                        .set(Chart::getErrorMsg, "消息发送失败: " + e.getMessage()));
            } catch (Exception ex) {
                log.error("标记失败状态也失败了, chartId={}", chartId, ex);
            }
            throw new RuntimeException("提交分析任务失败，请重试");
        }

        return chart;
    }
}
