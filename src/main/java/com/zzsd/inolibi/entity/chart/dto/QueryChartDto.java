package com.zzsd.inolibi.entity.chart.dto;

import com.zzsd.inolibi.entity.page.PageQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询图表dto
 * 有点多余
 */
@Data
@Schema(name = "ChartByAIDto", description = "ai生成图表dto")
public class QueryChartDto extends PageQueryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -582332207840702511L;

    /**
     * 图表名称
     */
    @Schema(description = "图表名称")
    private String chartName;
}