package com.fei.aitravelassistant.service;

import cn.hutool.core.util.StrUtil;
import com.fei.aitravelassistant.mapper.UserMapper;
import com.fei.aitravelassistant.model.entity.Users;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    private final String salt = "fei";

    private final int MAX_USAGE_COUNT = 10;
    /**
     * 注册新用户
     */
    public String register(String username, String password) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return "用户名或密码不能为空";
        }
        if (userMapper.findByUsername(username) != null) {
            return "用户名已存在";
        }

        Users user = new Users();
        user.setUsername(username);
        System.out.println("register password = " + password);
        password = DigestUtils.md5Hex(password + salt);
        System.out.println("md5password = " + password);
        user.setPasswordHash((password));
        user.setRole("USER");
        userMapper.insertUser(user);
        return "注册成功";
    }

    /**
     * 登录
     */
    public String login(String username, String password, HttpServletRequest request) {
        Users user = userMapper.findByUsername(username);
        if (user == null) {
            return "用户不存在";
        }
        System.out.println(user.getPasswordHash());
        System.out.println("login username = " + user.getUsername());
        System.out.println(user.getRole());
        System.out.println("login password = " + password);
        password = DigestUtils.md5Hex(password + salt);
        System.out.println("md5password = " + password);


        if (!password.equals(user.getPasswordHash())) {
            return "密码错误";
        }

        // 保存用户信息到 Session
        request.getSession().setAttribute("user", user);
        return "登录成功";
    }

    /**
     * 判断是否已登录
     */
    public boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("user") != null;
    }

    /**
     * 获取当前登录用户
     */
    public Users getCurrentUser(HttpServletRequest request) {
        return (Users) request.getSession().getAttribute("user");
    }

    /**
     * 注销（退出登录）
     */
    public void logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user"); //
    }

    /**
     * 检查并更新使用次数
     * @param userId
     * @return
     */
    public boolean checkUsageCount(long userId) {
        Users users = userMapper.findById(userId);
        if (users == null) {
            return false;
        }
        if (users.getUsageCount() >= MAX_USAGE_COUNT) {
            return false;
        }
        users.setUsageCount(users.getUsageCount() + 1);
        users.setLastUsedAt(new Date());
        userMapper.updateUser(users);
        return true;
    }

    public int getRemainingCount(long userId) {
        Users users = userMapper.findById(userId);
        if (users == null) {
            return 0;
        }
        return Math.max(MAX_USAGE_COUNT - users.getUsageCount(), 0);
    }
}
