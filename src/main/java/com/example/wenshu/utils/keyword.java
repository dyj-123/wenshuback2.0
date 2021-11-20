package com.example.wenshu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class keyword {
    public static Result getKeyWord(String s8,String cookie,String userAgent,String condition) {
        double pageId = wenshucode.getPageId();
        String verToken = wenshucode.getVerToken();

        Connection con = Jsoup.connect("https://wenshu.court.gov.cn/website/parse/rest.q4w");
        //登录的浏览器
        con.header("User-Agent", userAgent);
        con.header("Cookie", "SESSION="+cookie);
        con.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.header("Origin", "https://wenshu.court.gov.cn");
        con.header("Referer", "https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?s8=" + s8 + "&pageId=" + pageId);
        con.data("s8", s8);
        con.data("pageId", Double.toString(pageId));
        con.data("queryCondition", "[{\"key\":\"s8\",\"value\":\"" + s8 + "\"}]");
        //s45 关键字，s42 年份
        con.data("groupFields",condition);
        con.data("cfg", "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@leftDataItem");
        con.data("__RequestVerifificationToken", verToken);

        try {
            Document doc = con.timeout(500 * 1000).ignoreContentType(true).post();
            String res = doc.body().text().toString();
            JSONObject jsonObject = JSONObject.parseObject(res);
            String code = jsonObject.get("code").toString();
            if(code.equals("9")){
                return Result.build(400,"",null);
            }else {
                jsonObject = jsonObject.getJSONObject("result");
                JSONArray jsonArray = jsonObject.getJSONArray(condition);
                return Result.build(200,"",jsonArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.build(491,"过于频繁",null);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(491,"过于频繁",null);
        }
    }
}
