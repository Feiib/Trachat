package com.fei.aitravelassistant.controller;

import com.fei.aitravelassistant.agent.TravelManus;
import com.fei.aitravelassistant.app.TravelApp;
import com.fei.aitravelassistant.model.BaseResponse;
import com.fei.aitravelassistant.model.ErrorCode;
import com.fei.aitravelassistant.model.ResultUtils;
import com.fei.aitravelassistant.model.entity.Users;
import com.fei.aitravelassistant.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private TravelApp travelApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;
    @Resource
    private ChatService chatService;
    @Resource
    TravelManus travelManus;

    @GetMapping( "/travel_app/chat")
    public BaseResponse<String> doChatWithTravelApp(
            @RequestParam("message") String message,
            @RequestParam(required = false) String chatId,
            HttpServletRequest request
    ) {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            //  需要返回 data 行
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        try {
            Long userId = user.getId();
            String agentType = "TRAVEL_ASSISTANT";

            // 1 获取或创建会话
            Long sessionId = chatService.getOrCreateSession(userId, agentType, message, chatId);

            // 2 保存用户消息
            chatService.saveMessage(userId, sessionId, agentType, "user", message);
            String chatid = String.valueOf(sessionId);
            // 3 得到 AI 的回复
            String reply = travelApp.doChat(message, chatid);

            // 5 返回
             reply = "CHAT_ID" + sessionId + "\n" + "data: " + reply + "\n" + "[DONE]";

            chatService.saveMessage(userId, sessionId, agentType, "assistant", reply);
             return ResultUtils.success(reply);
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    @GetMapping("/manus/chat")
    public BaseResponse<String> doChatWithManus(String message,
                                                           @RequestParam(required = false) String chatId,
                                                           HttpServletRequest request) {
        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            //
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        try {
            Long userId = user.getId();
            String agentType = "MANUS_AGENT";
            // 1 获取或创建会话
            Long sessionId = chatService.getOrCreateSession(userId, agentType, message, chatId);
            // 2 保存用户消息
            chatService.saveMessage(userId, sessionId, agentType, "user", message);
            String reply = travelManus.run(message);
            // 3 保存 AI 消息
            chatService.saveMessage(userId, sessionId, agentType, "assistant", reply);
            reply = "CHAT_ID" + sessionId + "\n" + "data: " + reply + "\n" + "[DONE]";
            return ResultUtils.success(reply);
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }
}


