package com.fei.aitravelassistant.mapper;

import com.fei.aitravelassistant.model.entity.Users;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("""
        INSERT INTO users (username, password_hash, role, created_at)
        VALUES (#{username}, #{passwordHash}, CAST(#{role} AS user_role_enum), NOW())
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(Users user);

    @Select("SELECT * FROM users WHERE id = #{id}")
    Users findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    Users findByUsername(String username);

    @Update("UPDATE users SET usage_count = #{usageCount}, last_used_at = #{lastUsedAt} WHERE id = #{id}")
    void updateUser(Users users);
}
