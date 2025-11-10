package com.fei.aitravelassistant.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReAct 模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {
    /**
     * 决定下一步是否要行动
     *
     * @return
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     *
     * @return
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成-无需行动";
            }
            return act();
        } catch (Exception e) {
            return "发生异常：" + e.getMessage();
        }
    }

}
