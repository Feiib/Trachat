package com.fei.aitravelassistant.advisors;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * {Input_Query}
 * Read the question again: {Input_Query}
 * 重复读,可以提高大模型的推理能力
 *
 */
public class reReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return 0;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        Map<String, Object> adviserUserParam = new HashMap<>();
        adviserUserParam.put("Input_Query", request.userText());
        return AdvisedRequest.from(request).userText("{Input_Query}\n" +
                "Read the question again: {Input_Query}").userParams(adviserUserParam).build();
    }




    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        return chain.nextAroundCall(advisedRequest);
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        return chain.nextAroundStream(advisedRequest);
    }
}
