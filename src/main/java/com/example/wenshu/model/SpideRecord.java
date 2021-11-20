package com.example.wenshu.model;

import org.springframework.data.relational.core.sql.In;

import java.util.Date;

public class SpideRecord {
    private Integer id;

    private Date startTime;
    private Date endTime;
    private Integer wenshuNum;
    private String spiderName;
    private Integer status;
    private String condition;
    private String anjianType;
    private String progress;
    private String node;
    private Integer skip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getWenshuNum() {
        return wenshuNum;
    }

    public void setWenshuNum(Integer wenshuNum) {
        this.wenshuNum = wenshuNum;
    }

    public String getSpiderName() {
        return spiderName;
    }

    public void setSpiderName(String spderName) {
        this.spiderName = spderName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAnjianType() {
        return anjianType;
    }

    public void setAnjianType(String anjianType) {
        this.anjianType = anjianType;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }
}
