package com.fei.aitravelassistant.agent;

import com.fei.aitravelassistant.advisors.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class TravelManus extends ToolCallAgent {

    public TravelManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("travelManus");
        String SYSTEM_PROMPT = """
                 你是TravelManus，一个全能的人工智能助手，旨在解决用户提出的任何任务。您可以使用各种工具来高效地完成复杂的请求,你需要在有限的步骤内完成任务,请合理规划步骤
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                根据用户需求，主动选择最合适的工具或工具组合。对于复杂的任务，您可以分解问题并使用不同的工具逐步解决。使用每个工具后，
                清楚地解释执行结果并建议下一步。如果你想在任何时候停止交互，请使用`terminate`工具函数调用
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxStep(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
