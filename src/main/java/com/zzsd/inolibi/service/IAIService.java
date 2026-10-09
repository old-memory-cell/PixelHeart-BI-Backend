package com.zzsd.inolibi.service;

public interface IAIService {

    /**
     * 获取AI回答
     * @param question 问题
     * @param conversationId 会话id
     * @return ai回复
     */
    String getAIAnswer(String question , String conversationId);
}
