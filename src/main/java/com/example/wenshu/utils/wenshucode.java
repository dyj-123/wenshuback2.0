package com.example.wenshu.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;

public class wenshucode {



    public static Result getWenshu(String s8, String value,String userAgent,int pageNum, int pageSize,String cookie,String condition,String date){
       double pageId = getPageId();
       String verToken=getVerToken();
       Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
       String cipherText=getCiphertext();

        Connection con = Jsoup.connect("https://wenshu.court.gov.cn/website/parse/rest.q4w");
        //登录的浏览器
        con.header("User-Agent",userAgent);
        con.header("Cookie","SESSION="+cookie);
        con.header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        con.header("Origin","https://wenshu.court.gov.cn");
        con.header("Referer","https://wenshu.court.gov.cn/website/wenshu/181217BMTKHNT2W0/index.html?s8="+s8+"&pageId="+pageId);
        con.data("s8",s8);
        con.data("pageId", Double.toString(pageId));
        con.data("sortFields","s50:desc");
        con.data("ciphertext",cipherText);
        con.data("pageNum",String.valueOf(pageNum));
        con.data("pageSize",String.valueOf(pageSize));
        if(!date.equals("")){
            System.out.println(date);
            con.data("queryCondition","[{\"key\":\"s8\",\"value\":\""+s8+"\"},{\"key\":\""+condition+"\",\"value\":\""+value+"\"},{\"key\":\"cprq\",\"value\":\""+date+"\"}]");
        }else{

            con.data("queryCondition","[{\"key\":\"s8\",\"value\":\""+s8+"\"},{\"key\":\""+condition+"\",\"value\":\""+value+"\"}]");
        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, -1); //得到前一天
//        Date date = calendar.getTime();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String datestr = df.format(date)+" TO "+df.format(date);



        con.data("cfg","com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@queryDoc");
        con.data("__RequestVerifificationToken",verToken);

        try {
            Document doc = con.timeout(500*1000).ignoreContentType(true).post();
            String res = doc.body().text().toString();
            System.out.println(res);
            JSONObject jsonObject = JSONObject.parseObject(res);
            //对获取的结果解密
            String data = jsonObject.getString("result");
            String key = jsonObject.getString("secretKey");
            String iv = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
            System.out.println(decrypt(data,key,iv));
            res = decrypt(data,key,iv);
            jsonObject = JSONObject.parseObject(res);
            jsonObject = jsonObject.getJSONObject("queryResult");
            JSONArray jsonArray = jsonObject.getJSONArray("resultList");
        //    System.out.println(jsonArray);
            return Result.build(200,"",jsonArray);
        } catch (IOException e) {
//            e.printStackTrace();
//            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("status",491);
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.add(jsonObject);
            return Result.build(400,"错误",null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(491,"过于频繁",null);
        }
    }

    public static double getPageId(){
        double pageId;
        pageId=Math.random();
        //   System.out.println(pageId);
        return pageId;
    }

    public static String random(int size){
        String str="";
        char []arr=new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e',
                'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z'};
        for(int i=0;i<size;i++){
            str += arr[(int) Math.round(Math.random()*(arr.length-1))];
        }
        return  str;

    }
    /**
     * content: 加密内容
     * slatKey: 加密的盐，16位字符串
     * vectorKey: 加密的向量，16位字符串
     */
    public static String encrypt(String content, String slatKey, String vectorKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS7Padding");
        SecretKey secretKey = new SecretKeySpec(slatKey.getBytes(), "DESede");
        IvParameterSpec iv = new IvParameterSpec(vectorKey.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(encrypted);
    }
    /**
     * content: 解密内容(base64编码格式)
     * slatKey: 加密时使用的盐，16位字符串
     * vectorKey: 加密时使用的向量，16位字符串
     */
    public static String decrypt(String base64Content, String slatKey, String vectorKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS7Padding");
        SecretKey secretKey = new SecretKeySpec(slatKey.getBytes(), "DESede");
        IvParameterSpec iv = new IvParameterSpec(vectorKey.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] content = Base64.decodeBase64(base64Content);
        byte[] encrypted = cipher.doFinal(content);
        return new String(encrypted);
    }

    public static String getCiphertext(){
        Date date = new Date();
        String[] strNow1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString().split("-");
        String timestamp = String.valueOf(date.getTime());
        //System.out.println(timestamp);
        String salt = random(24);
        String year = strNow1[0];
        String month = strNow1[1];
        String day = strNow1[2];
        String iv = year+month+day;
        //   System.out.println(iv);
        String ciphertext="";
        try{
            String enc = encrypt(timestamp,salt,iv);
            String str = salt+iv+enc;
            //   System.out.println(str);
            ciphertext = convertStringToBinary(str);


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return ciphertext;

    }

    public static String convertStringToBinary(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        int count =0;
        for (char aChar : chars) {
            if(count!=0){
                result.append(" ");
            }
            count++;
            result.append(String.valueOf(Integer.toBinaryString(aChar)));	// char -> int, auto-cast zero pads
        }
        return result.toString();
    }

    public static String getVerToken(){
        return random(24);
    }


}
