package com.fei.aitravelassistant.agent;

import cn.hutool.core.util.StrUtil;
import com.fei.aitravelassistant.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public abstract class BaseAgent {
    private String name;

    private String systemPrompt;
    private String nextStepPrompt;

    private AgentState state = AgentState.IDLE;

    private ChatClient chatClient;

    private int maxStep = 20;
    private int currentStep = 0;

    private List<Message> messageList = new ArrayList<>();

/**
 * 执行用户提示并返回处理结果
 * 该方法接收一个用户提示字符串，执行相应操作后返回结果字符串
 *
 * @param userPrompt 用户输入的提示字符串，包含需要处理的内容
 * @return 返回处理后的结果字符串，具体内容取决于处理逻辑
 */
    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // 1、基础校验
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt");
        }
        // 2、执行，更改状态
        this.state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 保存结果列表
        List<String> results = new ArrayList<>();
        try {
            // 执行循环
            for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxStep);
                // 单步执行
                String stepResult = step();
                String displayResult = (this instanceof ToolCallAgent
                        && ((ToolCallAgent) this).getLastThinkResult() != null)
                        ? ((ToolCallAgent) this).getLastThinkResult()
                        : stepResult;
                String result = "Step " + stepNumber + ": " + displayResult;
                results.add(result);
            }
            // 检查是否超出步骤限制
            if (currentStep >= maxStep) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxStep + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("error executing agent", e);
            return "执行错误" + e.getMessage();
        } finally {
            // 3、清理资源
            this.clean();
        }
    }


    /**
     * 单个执行步骤
     *
     * @return
     */
    public abstract String step();
    protected void clean() {
        this.state = AgentState.IDLE;
        this.currentStep = 0;
        this.messageList.clear();
    }

}
