package com.zzsd.inolibi.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import org.springframework.web.multipart.MultipartFile;


/**
 * 图表信息表 服务类
 *
 * @author inoli
 * @since 2026-10-05
 */
public interface IChartService extends IService<Chart> {

    /**
     * 根据ai分析得到图表数据
     * @param chartByAIDto 生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    String getChartDataByAI(ChartByAIDto chartByAIDto, MultipartFile multipartFile);
}
