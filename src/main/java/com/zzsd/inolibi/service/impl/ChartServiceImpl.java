package com.zzsd.inolibi.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.mapper.ChartMapper;
import com.zzsd.inolibi.service.IAIService;
import com.zzsd.inolibi.service.IChartService;
import com.zzsd.inolibi.utils.AnalysisResult;
import com.zzsd.inolibi.utils.CurrentHolder;
import com.zzsd.inolibi.utils.ExcelUtils;
import com.zzsd.inolibi.utils.LlmResponseParser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图表信息表 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

    @Resource
    private IAIService aiService;


    /**
     * 根据ai分析得到图表数据
     *
     * @param chartByAIDto  生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    @Override
    public String getChartDataByAI(ChartByAIDto chartByAIDto, MultipartFile multipartFile) {
        // 1. 读取文件内容
        String csvStr = ExcelUtils.excelToCsv(multipartFile);
        // 2. 调用ai接口
        String goal = chartByAIDto.getGoal();
        String chartName = chartByAIDto.getChartName();
        String chartType = chartByAIDto.getChartType();
        Long userId = CurrentHolder.getCurrentId();
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
        return modelResponse;
    }
}
