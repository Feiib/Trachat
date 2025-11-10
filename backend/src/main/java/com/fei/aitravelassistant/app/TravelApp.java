package com.fei.aitravelassistant.app;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class TravelApp {
    private final ChatClient chatClient;

    @Resource
    private PgVectorStore pgVectorStore;

    @Resource
    private ToolCallback[] allTools;

    private static final String SYSTEM_PROMPT = """
            你是一个旅游助手，请根据用户输入, 提供旅游建议。
            以下是检索到的参考资料：
            {question_answer_context}
            ,回答请主要参考检索到的资料,如果答案不在资料中，可以调用工具搜索
            """;

    public TravelApp(ChatModel dashscopeChatmodel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        this.chatClient = ChatClient.builder(dashscopeChatmodel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    public String doChatWithRag(String message, String conversationId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
                .advisors(new SimpleLoggerAdvisor())
                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                .tools(allTools)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new SimpleLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public String doChat(String message, String chatId) {
        if (message.contains("查询") || message.contains("天气") || message.contains("票")
                || message.contains("火车") || message.contains("高铁")) {
                //工具调用
                return chatClient.prompt()
                        .user(message)
                        .system("你是一个旅游助手，可以调用工具帮助用户完成任务。")
                        .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                        .advisors(new SimpleLoggerAdvisor())
                        .tools(allTools)
                        .call()
                        .chatResponse()
                        .getResult()
                        .getOutput()
                        .getText();
        } else {
            //知识库
            return chatClient.prompt()
                    .user(message)
                    .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    .advisors(new SimpleLoggerAdvisor())
                    .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                    .call()
                    .content();
        }

    }
}
