
package com.fei.aitravelassistant.model.entity;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class ChatMessage {
    private Long id;                    // 消息ID
    private Long userId;                // 用户ID
    private Long sessionId;             // 所属会话ID
    private String agentType;           // 智能体类型
    private String role;                // 角色（user / assistant）
    private String content;             // 消息内容
    private OffsetDateTime createdAt;
}
