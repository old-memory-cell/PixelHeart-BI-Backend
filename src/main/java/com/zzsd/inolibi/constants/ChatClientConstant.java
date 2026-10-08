package com.zzsd.inolibi.constants;

/**
 * ChatClinet常量类
 */
public class ChatClientConstant {

    public static final String SYSTEM_PROMPT = """
            你是一名专业的数据分析师，可以根据用户的需求，分析目标与原始数据，会给出分析结论。
            你的目标：{goal},
            图表类型：{chartName},
            文件数据：{data}
            请根据以上内容，给出分析结论。
            """;

}
