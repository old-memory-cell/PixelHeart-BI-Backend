package com.zzsd.inolibi.entity.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页查询
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryDto {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页展示多少数据
     */
    private Integer pageSize = 10;

}
