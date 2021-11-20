package com.example.wenshu.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

public class Wenshu {

    @Field("title")
    private String title;
    @Field("court")
    private String court;
    @Field("reason")
    private String reason;
    @Field("num")
    private String num;
    @Field("id")
    private String Id;
    @Field("detailType")
    private String detailType;
    @Field("date")
    private String date;
    @Field("anjianType")
    private String anjianType;
    @Field("file")
    private String file;
    @Field("time")
    private Date time;
    @Field("spiderName")
    private String spiderName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAnjianType() {
        return anjianType;
    }

    public void setAnjianType(String anjianType) {
        this.anjianType = anjianType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getSpiderName() {
        return spiderName;
    }

    public void setSpiderName(String spiderName) {
        this.spiderName = spiderName;
    }
}
