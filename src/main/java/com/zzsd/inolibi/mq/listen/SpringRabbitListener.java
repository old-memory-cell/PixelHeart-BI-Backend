package com.zzsd.inolibi.mq.listen;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.enums.ChartStatusEnum;
import com.zzsd.inolibi.service.IAIService;
import com.zzsd.inolibi.service.IChartService;
import com.zzsd.inolibi.utils.AnalysisResult;
import com.zzsd.inolibi.utils.LlmResponseParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * rabbitmq监听类
 */
@Slf4j
@Component
public class SpringRabbitListener {

    @Resource
    private IChartService chartService;

    @Resource
    private IAIService aiService;

    /**
     * 监听队列
     *
     * @param chartId 图表id
     */
    @RabbitListener(queues = "chart.queue")
    public void listenBiAiQueue(Long chartId) {
        log.info("rabbitmq 监听类，接收到来自 chart.queue 的消息为：{}", chartId);

        // 更新图表状态
        boolean grabbed = chartService.update(Wrappers.<Chart>lambdaUpdate()
                .eq(Chart::getId, chartId)
                .eq(Chart::getStatus, ChartStatusEnum.PENDING)
                .set(Chart::getStatus, ChartStatusEnum.RUNNING));
        if (!grabbed) return;

        // 根据id查询图表信息
        Chart chart = chartService.getById(chartId) != null ? chartService.getById(chartId) : null;


        // 让ai分析
        try {
            String question = """
                    分析目标:%s
                    图表类型:%s
                    图表名称:%s
                    文件数据:%s
                    """.formatted(
                    chart.getGoal(),
                    chart.getChartType(),
                    chart.getChartName(),
                    chart.getChartData());

            String conversationId = UUID.randomUUID() + "-" + chart.getUserId();
            String resp = aiService.getAIAnswer(question, conversationId);
            AnalysisResult result = LlmResponseParser.parse(resp);

            // 4. 回写结果
            chartService.updateById(Chart.builder()
                    .id(chartId)
                    .genChart(result.echartsCode())
                    .genResult(result.conclusion())
                    .status(ChartStatusEnum.SUCCESS)
                    .build());
        } catch (Exception e) {
            log.error("AI 分析失败, chartId={}", chartId, e);
            chartService.updateById(Chart.builder()
                    .id(chartId)
                    .status(ChartStatusEnum.FAILED)
                    .errorMsg(e.getMessage())
                    .build());
        }

    }


}
