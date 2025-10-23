package com.fei.aitravelassistant.model.entity;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Date;

@Data
public class Users {
    private Long id;                    // 用户ID
    private String username;            // 用户名
    private String passwordHash;        // 密码哈希
    private String role;                // 用户角色（USER / ADMIN）
    private OffsetDateTime createdAt;   // 注册时间
    private Integer usageCount;      // 使用次数
    private Date lastUsedAt;  // 最后一次使用时间
}
