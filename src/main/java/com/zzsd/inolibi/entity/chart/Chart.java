package com.zzsd.inolibi.entity.chart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.zzsd.inolibi.entity.enums.ChartStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图表信息表
 *
 * @author inoli
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("chart")
@Schema(name = "Chart", description = "图表信息表")
@Builder
@JsonPropertyOrder({
        "id", "chartType", "goal", "chartData",
        "genChart", "genResult", "userId", "status",
        "createTime", "updateTime", "isDelete"
})
public class Chart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    /**
     * 图表状态
     */
    @Schema(description = "图表状态")
    @TableField("status")
    private ChartStatusEnum status;

    /**
     * 失败原因
     */
    @Schema(description = "失败原因")
    @TableField(exist = false)
    private String errorMsg;

    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户 id")
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    @TableField("is_delete")
    private Integer isDelete;
}