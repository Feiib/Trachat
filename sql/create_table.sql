-- ========================================
-- 1️⃣ 枚举类型定义
-- ========================================

-- 智能体类型（旅游助手、Manus智能体）
CREATE TYPE agent_type_enum AS ENUM ('TRAVEL_ASSISTANT', 'MANUS_AGENT');

-- 消息角色（用户或助手）
CREATE TYPE message_role_enum AS ENUM ('user', 'assistant');

-- 用户角色（普通用户或管理员）
CREATE TYPE user_role_enum AS ENUM ('USER', 'ADMIN');



-- ========================================
-- 2️⃣ 用户表 (users)
-- ========================================

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,                      -- 用户ID
                       username VARCHAR(255) UNIQUE NOT NULL,         -- 用户名（唯一）
                       password_hash VARCHAR(255) NOT NULL,           -- 密码哈希
                       role user_role_enum NOT NULL DEFAULT 'USER',   -- 用户角色（默认普通用户）
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()  -- 注册时间
);



-- ========================================
-- 3️⃣ 会话表 (chat_sessions)
-- ========================================

CREATE TABLE chat_sessions (
                               id BIGSERIAL PRIMARY KEY,                         -- 会话ID
                               user_id BIGINT NOT NULL,                          -- 所属用户
                               agent_type agent_type_enum NOT NULL,              -- 会话所属智能体类型
                               title VARCHAR(255),                               -- 可选标题（例如“重庆三日游规划”）
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),    -- 创建时间

                               CONSTRAINT fk_session_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users (id)
                                       ON DELETE CASCADE
);



-- ========================================
-- 4️⃣ 聊天消息表 (chat_messages)
-- ========================================

CREATE TABLE chat_messages (
                               id BIGSERIAL PRIMARY KEY,                                  -- 消息ID
                               user_id BIGINT NOT NULL,                                   -- 用户ID
                               session_id BIGINT NOT NULL,                                -- 所属会话ID
                               agent_type agent_type_enum NOT NULL,                       -- 智能体类型
                               role message_role_enum NOT NULL,                           -- 角色（user/assistant）
                               content TEXT NOT NULL,                                     -- 消息内容
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),             -- 创建时间

                               CONSTRAINT fk_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users (id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_session
                                   FOREIGN KEY (session_id)
                                       REFERENCES chat_sessions (id)
                                       ON DELETE CASCADE
);
alter table users add column usage_count int default 0;
alter table users add column last_used_at TIMESTAMPTZ;