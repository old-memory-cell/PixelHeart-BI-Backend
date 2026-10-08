package com.zzsd.inolibi.config;

import com.zzsd.inolibi.advisors.MyLogAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.zzsd.inolibi.constants.ChatClientConstant.SYSTEM_PROMPT;


/**
 * ChatClient的配置类
 */
@Configuration
public class ChatClientConfig {


    @Resource
    private ChatMemory chatMemory;


    /**
     * 创建ChatClient实例
     *
     * @param deepSeekChatModel deepseek大模型
     * @return ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatModel deepSeekChatModel) {
        return ChatClient.builder(deepSeekChatModel)
                .defaultAdvisors(
                        // 自定义日志顾问
                        MyLogAdvisor.builder().build(),
                        // 聊天记忆顾问
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

}
