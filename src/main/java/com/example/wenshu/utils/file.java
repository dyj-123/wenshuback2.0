package com.example.wenshu.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;

public class file {
    public static  String getHtml(String docId,String cookie,String userAgent){
        Connection con3 = Jsoup.connect("https://wenshu.court.gov.cn/down/one?docId="+docId);
        con3.header("User-Agent",userAgent);
        con3.header("Cookie","SESSION="+cookie);
        con3.header("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        con3.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        con3.header("Accept","gzip, deflate, br");
        Connection.Response res3 = null;
        try {

            res3 = con3.timeout(100*1000).method(Connection.Method.GET).ignoreContentType(true).execute();
            InputStream in = res3.bodyStream();

            return inputStreamToString(in);


        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String inputStreamToString(InputStream inputStream) {
        StringBuffer buffer = new StringBuffer();
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "GBK");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


}
