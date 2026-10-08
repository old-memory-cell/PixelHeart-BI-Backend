package com.zzsd.inolibi.controller;


import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.service.IChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图表信息表 前端控制器
 *
 * @author inoli
 * @since 2026-10-05
 */
@RestController
@RequestMapping("/chart")
@Tag(name = "图表信息管理", description = "图表信息管理")
public class ChartController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private IChartService chartService;

    /**
     * 根据ai分析得到图表数据
     * @param chartByAIDto 生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    @PostMapping("/getChartDataByAI")
    @Operation(summary = "根据AI获取图表数据")
    public Result<String> getChartDataByAI(ChartByAIDto chartByAIDto ,@RequestParam("file") MultipartFile multipartFile){
        String result = chartService.getChartDataByAI(chartByAIDto, multipartFile);
        return Result.success(result);
    }

}
