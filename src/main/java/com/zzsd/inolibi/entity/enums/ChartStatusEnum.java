package com.zzsd.inolibi.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * 图表生成状态
 */
@Getter
public enum ChartStatusEnum implements IEnum<Integer> {

    PENDING(0, "等待中"),
    RUNNING(1, "生成中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    /** 数据库存储值 */
    private final Integer value;
    /** 描述 */
    private final String desc;

    ChartStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据 value 获取枚举
     */
    public static ChartStatusEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        for (ChartStatusEnum status : values()) {
            if (Objects.equals(status.value, value)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 校验是否为有效状态值
     */
    public static boolean isValid(Integer value) {
        return of(value) != null;
    }
}