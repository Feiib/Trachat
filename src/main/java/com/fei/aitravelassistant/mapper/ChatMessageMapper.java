package com.fei.aitravelassistant.mapper;

import com.fei.aitravelassistant.model.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    /**
     * 根据会话ID获取消息列表
     */
    @Select("SELECT * FROM chat_messages " +
            "WHERE session_id = #{sessionId} " +
            "ORDER BY created_at ASC")
    List<ChatMessage> getMessagesBySessionId(Long sessionId);

    /**
     * 插入新消息
     */
    @Insert("INSERT INTO chat_messages (user_id, session_id, agent_type, role, content, created_at) " +
            "VALUES (#{userId}, #{sessionId}, CAST(#{agentType} AS agent_type_enum), " +
            "CAST(#{role} AS message_role_enum), #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMessage(ChatMessage chatMessage);

    /**
     * 删除会话的所有消息
     */
    @Delete("DELETE FROM chat_messages WHERE session_id = #{sessionId}")
    int deleteBySessionId(Long sessionId);

    /**
     * 获取会话的最后一条消息（用于生成标题等）
     */
    @Select("SELECT * FROM chat_messages " +
            "WHERE session_id = #{sessionId} " +
            "ORDER BY created_at DESC " +
            "LIMIT 1")
    ChatMessage getLastMessageBySessionId(Long sessionId);

    /**
     * 统计会话的消息数量
     */
    @Select("SELECT COUNT(*) FROM chat_messages WHERE session_id = #{sessionId}")
    int countMessagesBySessionId(Long sessionId);
}

