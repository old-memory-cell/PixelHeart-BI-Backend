package com.zzsd.inolibi.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.zzsd.inolibi.entity.chart.Chart;
import com.zzsd.inolibi.entity.chart.dto.ChartByAIDto;
import com.zzsd.inolibi.mapper.ChartMapper;
import com.zzsd.inolibi.service.IChartService;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图表信息表 服务实现类
 *
 * @author inoli
 * @since 2026-10-05
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements IChartService {

    @Resource
    private ChatClient chatClient;


    /**
     * 根据ai分析得到图表数据
     * @param chartByAIDto 生成图表dto
     * @param multipartFile 上传的文件
     * @return ai分析结果
     */
    @Override
    public String getChartDataByAI(ChartByAIDto chartByAIDto, MultipartFile multipartFile) {
        // TODO 进行ai调用。
        // 1. 读取文件内容

        // 2. 调用ai接口

        // 3. 保存文档内容到mysql

        // 3. 返回结果
        return "";
    }
}
