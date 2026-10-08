package com.zzsd.inolibi.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.mapper.ChartMapper;
import com.zzsd.inolibi.service.IChartService;

import org.springframework.stereotype.Service;

/**
 * 图表信息表 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

}
