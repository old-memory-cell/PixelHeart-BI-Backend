package com.zzsd.inolibi.entity.chart.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图表信息返回类
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("chart")
@Schema(name = "Chart", description = "图表信息表")
@Builder
public class BIPageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 图表类型
     */
    @Schema(description = "图表类型")
    @TableField("chart_type")
    private String chartType;

    /**
     * 图表名称
     */
    @Schema(description = "图表名称")
    @TableField("chart_name")
    private String chartName;

    /**
     * 分析目标
     */
    @Schema(description = "分析目标")
    @TableField("goal")
    private String goal;

    /**
     * 图表数据
     */
    @Schema(description = "图表数据")
    @TableField("chart_data")
    private String chartData;

    /**
     * 生成的图表数据
     */
    @Schema(description = "生成的图表数据")
    @TableField("gen_chart")
    private String genChart;

    /**
     * 生成的分析结论
     */
    @Schema(description = "生成的分析结论")
    @TableField("gen_result")
    private String genResult;

}