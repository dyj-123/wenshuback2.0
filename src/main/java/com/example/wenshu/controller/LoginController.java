package com.example.wenshu.controller;


import com.example.wenshu.model.SsoQuery;
import com.example.wenshu.service.UserService;
import com.example.wenshu.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: vote
 * @description: 登录控制器
 * @author: ggmr
 * @create: 2018-06-17 02:06
 */
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result loginSSO(@RequestBody SsoQuery ssoQuery, HttpServletRequest request) {
        return userService.loginSSO(request,ssoQuery);
    }

//    @GetMapping("/test")
//    public Result loginSSO(@RequestParam("code")String code, HttpServletRequest request, HttpServletResponse response) {
//        SsoQuery ssoQuery = new SsoQuery();
//        ssoQuery.setCode(code);
//        ssoQuery.setUrl("http://127.0.0.1:8081/v1/test");
//        return userService.loginSSO(request,ssoQuery,response);
//    }
//    @PostMapping("/filter")
//    public Result test( ) {
//        return Result.ok("恭喜你，验证成功啦，我可以返回数据给你");
//    }

}
