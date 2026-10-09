package com.zzsd.inolibi.service.impl;

import com.zzsd.inolibi.service.IAIService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

/**
 * ai接口实现类
 */
@Service
public class AIServiceImpl implements IAIService {

    @Resource
    private ChatClient chatClient;

    /**
     * 获取 ai 回复
     * @param question 问题
     * @param conversationId 会话id
     * @return 大模型回复
     */
    @Override
    public String getAIAnswer(String question, String conversationId) {
        return chatClient.prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
