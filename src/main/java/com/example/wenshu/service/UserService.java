package com.example.wenshu.service;

import com.alibaba.fastjson.JSONObject;
import com.example.wenshu.interceptor.UserContext;
import com.example.wenshu.mapper.ColledgeMapper;
import com.example.wenshu.mapper.UserMapper;
import com.example.wenshu.model.SsoQuery;
import com.example.wenshu.model.TokenResponse;
import com.example.wenshu.model.User;
import com.example.wenshu.utils.AuthTool;
import com.example.wenshu.utils.CommonUtil;
import com.example.wenshu.utils.JwtUtil;
import com.example.wenshu.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private AuthTool authUtil;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ColledgeMapper colledgeMapper;



    public Result loginSSO(HttpServletRequest request, SsoQuery ssoQuery) {
        if (CommonUtil.isStrNull(ssoQuery.getCode()) && CommonUtil.isStrNull(ssoQuery.getUrl())) {
            String token = request.getHeader("Authorization");
            if(CommonUtil.isStrNull(token)) {
                return Result.build(400,"不存在的token");

            } else {
                int res = jwtUtil.validateToken(token);
                if (res == 2){
                    User user = UserContext.getCurrentUser();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("identity",user.getType());
                    jsonObject.put("token",token);
                    jsonObject.put("name",user.getName());
//                    TokenResponse response = new TokenResponse();
//                    response.setIdentity(user.getType());
//                    response.setToken(token);
//                    response.setName(user.getName());
                    System.out.println(jsonObject);
                    return Result.build(200,"验证成功",jsonObject);
                } else {

                    return Result.build(400,"不存在的token","");
                }
            }
        }
        //获取TOKEN
        JSONObject tokenJson = authUtil.SSOGetToken(ssoQuery.getCode(), ssoQuery.getUrl());

        if (tokenJson == null) {
            return Result.build(400,"sso登录失败");
        }
        System.out.println(tokenJson);
        String token = tokenJson.getString("access_token");
        if (CommonUtil.isStrNull(token)) {
            return Result.build(400,"解析失败");
        }
        JSONObject userInfo = authUtil.SSOGetInfo(token);

        if (userInfo == null) {
            return Result.build(400,"sso登录失败");
        }
        String schoolCardId = userInfo.getString("userid");
        if (CommonUtil.isStrNull(token)) {
            return Result.build(400,"token无效");
        }
        User existedUser = userMapper.selectByPrimaryKey(Integer.parseInt(schoolCardId));
        System.out.println(existedUser);
        //String collegename = AuthTool.getUserCollege(schoolCardId);
        //String collegename = userInfo.getString("department");
        String newtoken = jwtUtil.createJwt(schoolCardId);
        System.out.println(newtoken);
        // 用户是否已存在,用户不存在则数据库添加用户
        if (existedUser == null) {
            // User user1 = AuthTool.getInfo(schoolCardId);
            User record = new User();
            record.setId(Integer.parseInt(schoolCardId));
            record.setType("0");
            record.setName(userInfo.getString("name"));
            record.setToken(newtoken);
            //学院号
           // record.setCollegeId(colledgeMapper.selectByCollegeName(userInfo.getString("department")));
            userMapper.addUser(record);
        }
        User user1 = userMapper.selectByPrimaryKey(Integer.parseInt(schoolCardId));
        User record = new User();
        record.setId(Integer.parseInt(schoolCardId));
        System.out.println(newtoken.split(" ")[1]);

    //    Cookie cookie = new Cookie("token",newtoken.split(" ")[1]);

        //TokenResponse response = new TokenResponse();
        //发送用户姓名
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("identity",user1.getType());

        jsonObject.put("token",newtoken);
        jsonObject.put("name",user1.getName());

        //发送至多赞数和对应的项目名称
        return Result.build(200,"成功",jsonObject);
    }


}
