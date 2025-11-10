package com.fei.aitravelassistant.mapper;

import com.fei.aitravelassistant.model.entity.ChatSession;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatSessionMapper {

    /**
     * 根据ID查找会话
     */
    @Select("SELECT * FROM chat_sessions WHERE id = #{id}")
    ChatSession findById(Long id);

    /**
     * 插入新会话
     */
    @Insert("INSERT INTO chat_sessions (user_id, agent_type, title, created_at) " +
            "VALUES (#{userId}, CAST(#{agentType} AS agent_type_enum), #{title}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSession(ChatSession chatSession);

    /**
     * 获取用户的会话列表
     */
    @Select("SELECT * FROM chat_sessions " +
            "WHERE user_id = #{userId} AND agent_type = CAST(#{agentType} AS agent_type_enum) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}")
    List<ChatSession> findSessionsByUserId(Long userId, String agentType, Integer offset, Integer size);

    /**
     * 更新会话标题
     */
    @Update("UPDATE chat_sessions SET title = #{title} WHERE id = #{sessionId}")
    int updateTitle(@Param("sessionId") Long sessionId, @Param("title") String title);

    /**
     * 删除会话
     */
    @Delete("DELETE FROM chat_sessions WHERE id = #{sessionId}")
    int deleteById(Long sessionId);

    /**
     * 验证会话所有权
     */
    @Select("SELECT COUNT(*) FROM chat_sessions WHERE id = #{sessionId} AND user_id = #{userId}")
    int validateSessionOwnership(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
    /**
     * 按用户ID和智能体类型查询会话
     */
    @Select("SELECT * FROM chat_sessions " +
            "WHERE user_id = #{userId} AND agent_type = CAST(#{agentType} AS agent_type_enum) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}")
    List<ChatSession> findSessionsByUserIdAndAgentType(Long userId, String agentType, Integer offset, Integer size);
}
