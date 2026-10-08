package com.zzsd.inolibi;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatClientTest {

    @Resource
    private ChatClient chatClient;

    /**
     * 测试ai是否可以调用
     */
    @Test
    public void test() {
        String conversationId = UUID.randomUUID().toString();
        String result = chatClient.prompt()
                .user("你好")
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
        System.out.println(result);
    }

}
