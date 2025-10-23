package com.fei.aitravelassistant.model.entity;

import lombok.Data;

import java.time.OffsetDateTime;
@Data
public class ChatSession {
    private Long id;                    // 会话ID
    private Long userId;                // 所属用户ID
    private String agentType;           // 智能体类型（TRAVEL_ASSISTANT / MANUS_AGENT）
    private String title;               // 会话标题
    private OffsetDateTime createdAt;   // 创建时间
}
