package com.zzsd.inolibi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.common.RedissonRateLimiter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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
    private ThreadPoolExecutor threadPoolExecutor;


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
                .eq(ObjectUtil.isNotEmpty(queryChartDto.getChartType()), Chart::getChartType, queryChartDto.getChartType());
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

        // 2. 异步调用ai接口
        Long chartId = chart.getId();
        String conversationId = UUID.randomUUID() + "-" + userId;

        // 使用线程池进行调用ai分析
        CompletableFuture
                .runAsync(
                        () -> doAsyncAnalysis(chartId, chartByAIDto, csvStr, conversationId, userId),
                        threadPoolExecutor)
                // 出现异常需要处理
                .exceptionally(ex -> {
                    log.error("提交异步分析任务失败, chartId={}", chartId, ex);
                    // 兜底：把状态改为失败
                    update(Wrappers.<Chart>lambdaUpdate()
                            .eq(Chart::getId, chartId)
                            .set(Chart::getStatus, ChartStatusEnum.FAILED)
                            .set(Chart::getErrorMsg, "任务提交失败: " + ex.getMessage()));
                    return null;
                });;

        return chart;
    }

    /**
     * 异步调用ai接口
     *
     * @param chartId 图表id
     * @param dto dto
     * @param csvStr 文件数据
     * @param conversationId 会话id
     * @param userId 用户id
     */
    public void doAsyncAnalysis(Long chartId, ChartByAIDto dto, String csvStr,
                                String conversationId, Long userId) {
        // 更新为 RUNNING（可选）
        boolean updated = update(Wrappers.<Chart>lambdaUpdate()
                .eq(Chart::getId, chartId)
                .eq(Chart::getUserId , userId)
                .set(Chart::getStatus, ChartStatusEnum.RUNNING));

        if (!updated) {
            log.error("更新状态失败, chartId={}", chartId);
            return;
        }

        try {
            String question = """
                    分析目标:%s
                    图表类型:%s
                    图表名称:%s
                    文件数据:%s
                    """.formatted(dto.getChartType(), dto.getChartName(), dto.getGoal(), csvStr);

            String modelResponse = aiService.getAIAnswer(question, conversationId);
            AnalysisResult result = LlmResponseParser.parse(modelResponse);

            // 更新结果
            Chart update = Chart.builder()
                    .id(chartId)
                    .genChart(result.echartsCode())
                    .genResult(result.conclusion())
                    .status(ChartStatusEnum.SUCCESS)
                    .build();
            this.updateById(update);
        } catch (Exception e) {
            log.error("AI 分析失败, chartId={}", chartId, e);
            Chart update = Chart.builder()
                    .id(chartId)
                    .status(ChartStatusEnum.FAILED)
                    .errorMsg(e.getMessage())
                    .build();
            this.updateById(update);
        }
    }
}
