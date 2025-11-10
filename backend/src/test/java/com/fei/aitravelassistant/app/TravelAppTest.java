package com.fei.aitravelassistant.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class TravelAppTest {
    @Resource
    private TravelApp travelApp;

    @Test
    void doChatWithRag() {
        String chatID = UUID.randomUUID().toString();
       /* String message = "你好，我是bb";
        String result = travelApp.doChatWithRag(message, chatID);
        String message2 = "我想去北京,你有什么建议吗";
        String message3 = "厦门怎么样,有什么好玩的";
        String result2 = travelApp.doChatWithRag(message2, chatID);
        String result3 = travelApp.doChatWithRag(message3, chatID);*/
        String message = "我在武汉,  我想去兰州旅游,给我一个旅游攻略和出行建议,总结完毕后通过PDF输出";
        String result = travelApp.doChatWithRag(message, chatID);
        System.out.println(result);
    }
    @Test
    void doChat() {
        String chatID = UUID.randomUUID().toString();
   //    String message = "北京的天气怎么样";
        String message = "";
        String string = travelApp.doChatWithRag(message, chatID);
        System.out.println(string);
    }

    @Test
    void doChatWithMcp() {
        String chatID = UUID.randomUUID().toString();
         String message = "北京的天气怎么样";
        //String message = "帮我查询武汉到兰州的车票情况";
        String string = travelApp.doChatWithRag(message, chatID);
        System.out.println(string);
    }
}