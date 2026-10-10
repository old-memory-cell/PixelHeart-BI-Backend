package com.zzsd.inolibi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.common.RedissonRateLimiter;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.entity.chart.dto.QueryChartDto;
import com.zzsd.inolibi.entity.chart.vo.ChartVo;
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
}
