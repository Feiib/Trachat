package com.fei.aitravelassistant.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class travelManusTest {

    @Resource
    private TravelManus travelManus;
    @Test
    void run(){
      //  String userPrompt = "我在武汉,我想去兰州旅游,给我一个旅游攻略和出行建议";
        String userPrompt = "查询武汉到兰州的车票情况";
        String run = travelManus.run(userPrompt);
        System.out.println(run);
    }
}