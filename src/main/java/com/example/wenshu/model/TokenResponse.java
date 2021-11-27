package com.example.wenshu.model;


import java.util.List;

/**
 * @program: vote
 * @description: 登录接口返回信息
 * @author: ggmr
 * @create: 2018-06-17 02:18
 */

public class TokenResponse {
    private String identity;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String token;

    private String collegeName;
    private Integer collegeId;


}
