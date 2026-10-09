package com.zzsd.inolibi.entity.chart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ai生成图表dto
 */
@Data
@Schema(name = "ChartByAIDto", description = "ai生成图表dto")
public class ChartByAIDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -582332207840702511L;

    /**
     * 图表名称
     */
    @Schema(description = "图表名称")
    private String chartName;

    /**
     * 分析目标
     */
    @Schema(description = "分析目标")
    private String goal;

    /**
     * 图表类型
     */
    @Schema(description = "图表类型")
    private String chartType;

}