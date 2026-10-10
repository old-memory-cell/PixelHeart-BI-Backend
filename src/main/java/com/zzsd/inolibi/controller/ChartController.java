package com.zzsd.inolibi.controller;


import com.zzsd.inolibi.common.Result;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.entity.chart.dto.QueryChartDto;
import com.zzsd.inolibi.entity.chart.vo.ChartVo;
import com.zzsd.inolibi.entity.page.PageResult;
import com.zzsd.inolibi.service.IChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
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
    private IChartService chartService;

    /**
     * 根据ai分析得到图表数据
     *
     * @param chartByAIDto  生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    @PostMapping("/getChartDataByAI")
    @Operation(summary = "根据AI获取图表数据")
    public Result<Chart> getChartDataByAI(ChartByAIDto chartByAIDto,
                                          @RequestParam("file") MultipartFile multipartFile) {
        Chart result = chartService.getChartDataByAI(chartByAIDto, multipartFile);
        return Result.success(result);
    }

    /**
     * 让ai异步分析得到图表结果
     *
     * @param chartByAIDto  生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    @PostMapping("/getChartDataByAIAsync")
    @Operation(summary = "根据AI异步获取图表数据")
    public Result<Chart> getChartDataByAIAsync(ChartByAIDto chartByAIDto,
                                               @RequestParam("file") MultipartFile multipartFile) {
        Chart result = chartService.getChartDataByAIAsync(chartByAIDto, multipartFile);
        return Result.success(result);
    }

    /**
     * 获取图表列表
     *
     * @param queryChartDto 查询图表dto
     * @return 图表列表
     */
    @PostMapping("/listCharts")
    @Operation(summary = "获取图表列表")
    public Result<PageResult<ChartVo>> listCharts(@RequestBody QueryChartDto queryChartDto) {
        PageResult<ChartVo> chartVoList = chartService.listCharts(queryChartDto);
        return Result.success(chartVoList);
    }

}
