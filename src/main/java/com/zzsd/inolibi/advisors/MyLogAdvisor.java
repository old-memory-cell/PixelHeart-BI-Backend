package com.zzsd.inolibi.advisors;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

/**
 * 自定义日志顾问
 */
@Slf4j
@Builder
//@Component
public class MyLogAdvisor implements CallAdvisor, StreamAdvisor {

    // 日志顾问的顺序
    private final int order = BaseAdvisor.HIGHEST_PRECEDENCE + 400;

    /**
     * 阻塞调用的方法
     *
     * @param chatClientRequest 请求
     * @param callAdvisorChain  调用链
     * @return 响应
     */
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        logRequest(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        logResponse(chatClientResponse);
        return chatClientResponse;
    }

    /**
     * 流调用的方法
     *
     * @param chatClientRequest  请求
     * @param streamAdvisorChain 调用链
     * @return 响应
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) {
        logRequest(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    /**
     * 记录请求日志
     *
     * @param request 请求
     */
    protected void logRequest(ChatClientRequest request) {
        log.info("AI Request {}", request.prompt().getUserMessage());
    }


    /**
     * 记录响应日志
     *
     * @param chatClientResponse 响应
     */
    protected void logResponse(ChatClientResponse chatClientResponse) {
        log.info("AI Response {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
    }

    /**
     * 获取顾问名称
     *
     * @return String
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取顾问顺序
     *
     * @return order
     */
    @Override
    public int getOrder() {
        return order;
    }
}
