package com.fei.aitravelassistant.controller;

import com.fei.aitravelassistant.model.BaseResponse;
import com.fei.aitravelassistant.model.ErrorCode;
import com.fei.aitravelassistant.model.ResultUtils;
import com.fei.aitravelassistant.model.entity.ChatMessage;
import com.fei.aitravelassistant.model.entity.ChatSession;
import com.fei.aitravelassistant.model.entity.Users;
import com.fei.aitravelassistant.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatSessionController {

    @Resource
    private ChatService chatService;

    /**
     * 获取会话列表（通用接口，通过agentType区分应用）
     */
    @GetMapping("/sessions")
    public BaseResponse<List<ChatSession>> getSessions(
            @RequestParam String agentType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            HttpServletRequest request) {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 验证agentType合法性
        if (!isValidAgentType(agentType)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "无效的智能体类型");
        }

        List<ChatSession> sessions = chatService.getSessionsByUserId(
                user.getId(), agentType, page, size);

        return ResultUtils.success(sessions);
    }

    /**
     * 删除会话（通用接口）
     */
    @DeleteMapping("/session")
    public BaseResponse<Boolean> deleteSession(
            @RequestParam Long sessionId,
            HttpServletRequest request) {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        boolean success = chatService.deleteSession(sessionId, user.getId());
        return ResultUtils.success(success);
    }

    /**
     * 更新会话标题（通用接口）
     */
    @PutMapping("/session/title")
    public BaseResponse<Boolean> updateSessionTitle(
            @RequestParam Long sessionId,
            @RequestParam String title,
            HttpServletRequest request) {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean success = chatService.updateSessionTitle(sessionId, user.getId(), title);
        return ResultUtils.success(success);
    }

    /**
     * 清空会话消息（通用接口）
     */
    @DeleteMapping("/session/messages")
    public BaseResponse<Boolean> clearSessionMessages(
            @RequestParam Long sessionId,
            HttpServletRequest request) {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean success = chatService.clearSessionMessages(sessionId, user.getId());
        return ResultUtils.success(success);
    }

    /**
     * 获取聊天历史（通用接口）
     */
    @GetMapping("/history")
    public BaseResponse<List<ChatMessage>> getChatHistory(
            @RequestParam Long sessionId,
            HttpServletRequest request) {

        Users user = (Users) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        List<ChatMessage> history = chatService.getHistory(sessionId, user.getId());
        return ResultUtils.success(history);
    }

    /**
     * 验证agentType合法性
     */
    private boolean isValidAgentType(String agentType) {
        return "TRAVEL_ASSISTANT".equals(agentType) || "MANUS_AGENT".equals(agentType);
    }
}
