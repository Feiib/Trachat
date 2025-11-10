package com.fei.aitravelassistant.service;

import com.fei.aitravelassistant.mapper.ChatMessageMapper;
import com.fei.aitravelassistant.mapper.ChatSessionMapper;
import com.fei.aitravelassistant.mapper.UserMapper;
import com.fei.aitravelassistant.model.entity.ChatMessage;
import com.fei.aitravelassistant.model.entity.ChatSession;
import com.fei.aitravelassistant.model.entity.Users;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ChatService {
    @Resource
    private  ChatSessionMapper chatSessionMapper;
    @Resource
    private  ChatMessageMapper chatMessageMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    /**
     * 获取或创建会话
     */
    public Long getOrCreateSession(Long userId, String agentType, String message, String chatId) {
        // 验证现有会话
        Users users = userMapper.findById(userId);
        if (users.getRole().equals("USER")) {
            if (!userService.checkUsageCount(userId)) {
                throw new RuntimeException("用户使用次数已达上限");
            }
        }
        if (isValidChatId(chatId, userId)) {
            return Long.parseLong(chatId);
        }

        // 创建新会话
        String title = generateTitleFromMessage(message);
        ChatSession newSession = new ChatSession();
        newSession.setUserId(userId);
        newSession.setAgentType(agentType);
        newSession.setTitle(title);

        chatSessionMapper.insertSession(newSession);
        return newSession.getId();
    }

    /**
     * 验证chatId是否有效(会话是否存在且属于该用户)
     */
    private boolean isValidChatId(String chatId, Long userId) {
        if (chatId == null || chatId.trim().isEmpty()) {
            return false;
        }

        try {
            long sessionId = Long.parseLong(chatId.trim());
            int count = chatSessionMapper.validateSessionOwnership(sessionId, userId);
            return count > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

     /**  * 获取会话列表（按应用类型过滤）
            */
    public List<ChatSession> getSessionsByUserId(Long userId, String agentType, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return chatSessionMapper.findSessionsByUserIdAndAgentType(userId, agentType, offset, size);
    }

    /**
     * 删除会话
     */
    public boolean deleteSession(Long sessionId, Long userId) {
        if (chatSessionMapper.validateSessionOwnership(sessionId, userId) > 0) {
            return chatSessionMapper.deleteById(sessionId) > 0;
        }
        return false;
    }

    /**
     * 更新会话标题
     */
    public boolean updateSessionTitle(Long sessionId, Long userId, String title) {
        if (chatSessionMapper.validateSessionOwnership(sessionId, userId) > 0) {
            return chatSessionMapper.updateTitle(sessionId, title) > 0;
        }
        return false;
    }

    /**
     * 清空会话消息
     */
    public boolean clearSessionMessages(Long sessionId, Long userId) {
        if (chatSessionMapper.validateSessionOwnership(sessionId, userId) > 0) {
            return chatMessageMapper.deleteBySessionId(sessionId) > 0;
        }
        return false;
    }

    /**
     * 获取历史记录（带权限验证）
     */
    public List<ChatMessage> getHistory(Long sessionId, Long userId) {
        if (chatSessionMapper.validateSessionOwnership(sessionId, userId) > 0) {
            return chatMessageMapper.getMessagesBySessionId(sessionId);
        }
        return Collections.emptyList();
    }

    /**
     * 保存消息
     */
    public void saveMessage(Long userId, Long sessionId, String agentType, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setSessionId(sessionId);
        message.setAgentType(agentType);
        message.setRole(role);
        message.setContent(content);

        chatMessageMapper.insertMessage(message);
    }

    private String generateTitleFromMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "新对话";
        }
        String trimmed = message.trim();
        return trimmed.length() > 20 ? trimmed.substring(0, 20) + "..." : trimmed;
    }
}

