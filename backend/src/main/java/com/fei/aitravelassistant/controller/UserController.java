package com.fei.aitravelassistant.controller;


import com.fei.aitravelassistant.model.BaseResponse;
import com.fei.aitravelassistant.model.ErrorCode;
import com.fei.aitravelassistant.model.ResultUtils;
import com.fei.aitravelassistant.model.entity.Users;
import com.fei.aitravelassistant.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 注册
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<String> register(@RequestParam String username, @RequestParam String password) {
        String register = userService.register(username, password);
        return ResultUtils.success(register);
    }

    /**
     * 登录
     * @param username
     * @param password
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {
        String login = userService.login(username, password, request);
        return ResultUtils.success(login);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResultUtils.success("退出登录成功");
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/me")
    public Object currentUser(HttpServletRequest request) {
        Users user = userService.getCurrentUser(request);
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }
}
