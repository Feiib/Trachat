package com.fei.aitravelassistant.demo.invoke;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;

public class SpringAiInvoke implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        AssistantMessage result = dashScopeChatModel.call(new Prompt("你好"))
                .getResult()
                .getOutput();
        System.out.println(result);
    }

    private ChatModel dashScopeChatModel;

}
