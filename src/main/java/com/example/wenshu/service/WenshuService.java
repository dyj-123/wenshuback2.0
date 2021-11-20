package com.example.wenshu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.wenshu.mapper.ConditionMapper;
import com.example.wenshu.mapper.SpideRecordMapper;
import com.example.wenshu.mapper.WenshuMapper;
import com.example.wenshu.model.SpideRecord;
import com.example.wenshu.model.Wenshu;
import com.example.wenshu.model.wenshuModel;
import com.example.wenshu.utils.Result;
import com.example.wenshu.utils.file;
import com.example.wenshu.utils.keyword;
import com.example.wenshu.utils.wenshucode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.util.*;

@Service
public class WenshuService {
    @Resource
    private WenshuMapper wenshuMapper;
    @Resource
    private SolrClient solrClient;
    @Resource
    private ConditionMapper conditionMapper;

    @Resource
    private SpideRecordMapper spideRecordMapper;
//
//    private String curThread="";
//    private Integer num=0;
//    Date startTime;
//    Date endTime;

    public JSONObject getWeneshu(Wenshu wenshu, int curPage, int pageSize){
        long total;
        Page page =PageHelper.startPage(curPage, pageSize);

        List<Wenshu> wenshuList = wenshuMapper.selectByType(wenshu);
        PageInfo<Wenshu> pageInfo = new PageInfo<>(wenshuList);
        total = pageInfo.getTotal();

        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(wenshuList));

        JSONObject  jsonObject = new JSONObject();
        jsonObject.put("wenshulist",jsonArray);
        jsonObject.put("total",total);

        return jsonObject;
    }

    public  JSONObject getNum(){

        HashMap<String,Integer> map = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        String []anjianType = new String[]{"01","02","03","04","05","06","07","08","09","10","11","99"};
        HashMap<String,String> map1 = new HashMap<>();

        for(String type:anjianType){
            int num = wenshuMapper.numByType(type);

            map.put(type,num);
        }
        for(HashMap.Entry<String,Integer> entry : map.entrySet()){
            JSONObject jsonObject1 = new JSONObject();
            System.out.println(entry.getKey());
            String type1 = wenshuMapper.findNameById(entry.getKey());

            jsonObject1.put("name",type1);
            jsonObject1.put("value",entry.getValue());
            jsonArray.add(jsonObject1);

        }
        jsonObject.put("wenshuNum",jsonArray);
        return jsonObject;
    }

    public String getHtml(String Id){
        return wenshuMapper.getWenshuHtml(Id);
    }

    public JSONObject getCurDateWenshu(){
        String vertoken = wenshucode.getVerToken();

        Connection con = Jsoup.connect("https://wenshu.court.gov.cn/website/parse/rest.q4w");
        //登录的浏览器
        con.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.73");
        con.data("__RequestVerificationToken",vertoken);
        con.data("cfg","com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@wsCountSearch");

        try {
            Connection.Response res = con.method(Connection.Method.POST).ignoreContentType(true).execute();
            String result = res.body();
            JSONObject jsonObject = JSON.parseObject(result);
            System.out.println(JSON.parseObject(jsonObject.get("result").toString()));
            return JSON.parseObject(jsonObject.get("result").toString());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public Result spiderWenshu(String cookie, String userAgent,String date,String node) throws InterruptedException {
        Integer num = 0;
        Integer skip = 0;
        String[] anjiantype = new String[]{"01","02","03","04", "05", "06", "07","08", "09", "10", "11", "99"};
        String[] conditions = new String[]{"s6","s4","s8","s10","s33","s42","s45"};
   //     String[] datas = new String[] {"01","02","03","04","05","09"};
        Integer pageSize = 15;
        Date startTime = new Date();
        Date endTime;
        int count = 0;
        Thread.currentThread().setName(String.valueOf(startTime.getTime()));
        System.out.println(Thread.currentThread().getName());
        SpideRecord spideRecord = new SpideRecord();
        spideRecord.setStartTime(startTime);
        spideRecord.setWenshuNum(num);
        spideRecord.setSkip(skip);
//        System.out.println(anjianType);
//        spideRecord.setAnjianType(anjianType);
        spideRecord.setNode(node);
        spideRecord.setSpiderName(Thread.currentThread().getName());
//        String str = "";
//        for (String condition : conditions) {
//            str += condition;
//            str += " ";
//        }
//        System.out.println(str);
//        spideRecord.setCondition(str);
        spideRecord.setStatus(1);
        spideRecordMapper.addRecord(spideRecord);

        while(true) {
            Random r = new Random();
            int conditionR1 = r.nextInt(5); // 生成[0,5]区间的整数
            int dataR = r.nextInt(5);
            int typeR = r.nextInt(11);
            String condition = conditions[conditionR1];

            System.out.println(condition);
            String type = anjiantype[typeR];
          //  String data = datas[dataR];
            spideRecord.setCondition(condition);
            spideRecord.setAnjianType(type);

                    Result res = keyword.getKeyWord(type, cookie, userAgent, condition);//获取侧边栏
                    System.out.println(res.getStatus());
                    if(res.getStatus()==491){
                        endTime = new Date();
                        spideRecord.setEndTime(endTime);
                        spideRecord.setWenshuNum(num);
                        spideRecord.setSkip(skip);
                        spideRecord.setStatus(5);
                        spideRecordMapper.editRecord(spideRecord);
                        return Result.build(200, "过于频繁，爬虫结束");
                    }else if (res.getStatus() == 400) {//cookie过期
                        if (num != 0||skip!=0) {//如果爬取到数据，更新num
                            endTime = new Date();
                            spideRecord.setEndTime(endTime);
                            spideRecord.setWenshuNum(num);
                            spideRecord.setSkip(skip);
                            spideRecord.setStatus(2);
                            spideRecordMapper.editRecord(spideRecord);
                            return Result.build(200, "权限过期，爬虫结束");

                        }
                        endTime = new Date();
                        spideRecord.setEndTime(endTime);
                        spideRecord.setWenshuNum(0);
                        spideRecord.setStatus(3);
                        spideRecordMapper.editRecord(spideRecord);
                        return Result.build(400, "没有权限，请检查输入是否正确");

                    } else {
                        JSONArray keys = JSON.parseArray(res.getData().toString());
                        for (int k = 0; k < keys.size(); k++) {
                            JSONObject jsonObject1 = JSON.parseObject(keys.get(k).toString());
                            String value = jsonObject1.getString("value");
                            System.out.println("正在爬取" + type + value);
                            for (int i = 1; i <= 100; i++) {
                                System.out.println("第" + String.valueOf(i) + "页，");
                                String progress =conditionMapper.findNameByXuhao(condition)+" "+wenshuMapper.findNameById(type) + " " + value + " 第" + i + "页";
                                spideRecord.setProgress(progress);
                                spideRecordMapper.editRecord(spideRecord);
                                //开始爬虫
                                Result spiderResult = wenshucode.getWenshu(type, value, userAgent, i, 15, cookie, condition, date);
                                if (spiderResult.getStatus()==400) {
                                    continue;
                                }else if(spiderResult.getStatus()==491){
                                    endTime = new Date();
                                    spideRecord.setEndTime(endTime);
                                    spideRecord.setWenshuNum(num);
                                    spideRecord.setStatus(5);
                                    spideRecordMapper.editRecord(spideRecord);
                                    return Result.build(200, spiderResult.getMsg());

                                }
                                else {
                                    JSONArray jsonArray = (JSONArray) spiderResult.getData();
                                    //   System.out.println(jsonArray);
                                    for (int j = 0; j < jsonArray.size(); j++) {
                                        System.out.println(Thread.currentThread().isInterrupted());

                                        Wenshu wenshu = new Wenshu();
                                        String record = jsonArray.get(j).toString();
                                        JSONObject jsonObject = JSON.parseObject(record);
                                        wenshu.setTitle(jsonObject.getString("1"));
                                        wenshu.setCourt(jsonObject.getString("2"));
                                        wenshu.setNum(jsonObject.getString("7"));
                                        wenshu.setReason(jsonObject.getString("26"));
                                        wenshu.setId(jsonObject.getString("rowkey"));
                                        wenshu.setDate(jsonObject.getString("31"));
                                        wenshu.setDetailType(jsonObject.getString("9"));
                                        wenshu.setAnjianType(type);
                                        Date timeNow = new Date();
                                        wenshu.setTime(timeNow);
                                        wenshu.setSpiderName(Thread.currentThread().getName());
                                        try {
                                            String html = file.getHtml(jsonObject.getString("rowkey"), cookie, userAgent);
                                            wenshu.setFile(html);
                                            wenshuMapper.insert(wenshu);
//                                            num++;
//                                            spideRecord.setWenshuNum(num);
//                                            spideRecordMapper.editRecord(spideRecord);

                                        } catch (Exception e) {
                                            skip++;
                                            spideRecord.setSkip(skip);
                                            spideRecordMapper.editRecord(spideRecord);
                                        }
                                        try {//插入至solr
                                            SolrInputDocument document = new SolrInputDocument();
                                            document.addField("id", wenshu.getId());
                                            document.addField("title", wenshu.getTitle());
                                            document.addField("anjianType", wenshu.getAnjianType());
                                            document.addField("court", wenshu.getCourt());
                                            document.addField("reason", wenshu.getReason());
                                            document.addField("num", wenshu.getNum());
                                            document.addField("date", wenshu.getDate());
                                            document.addField("file", wenshu.getFile());
                                            document.addField("spiderName",wenshu.getSpiderName());
                                            UpdateRequest updateRequest = new UpdateRequest();
                                            updateRequest.setBasicAuthCredentials("solr", "SolrRocks");
                                            updateRequest.add(document);
                                            UpdateResponse response = updateRequest.process(solrClient, "my_core");
                                            updateRequest.commit(solrClient, "my_core");
                                            num++;
                                            spideRecord.setWenshuNum(num);
                                            spideRecordMapper.editRecord(spideRecord);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (SolrServerException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            //  System.out.println(Thread.currentThread().getName());
                                            Thread.sleep(1500);
                                        } catch (InterruptedException e) {
                                            //  e.printStackTrace();
                                            System.out.println("spide has interputed");
                                            endTime = new Date();
                                            spideRecord.setEndTime(endTime);
                                            spideRecord.setWenshuNum(num);
                                            spideRecord.setStatus(0);
                                            spideRecordMapper.editRecord(spideRecord);
                                            return Result.build(200, "爬虫结束");
                                        }

                                    }


                                }
                            }
                        }
                    }

        }


    }

    public JSONObject getWenshuBySpiderName(String spiderName,Integer curPage,Integer pageSize){

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("spiderName:"+spiderName);
        Integer start = (curPage-1)*pageSize;
        solrQuery.setStart(start);
        solrQuery.setRows(pageSize);
        QueryResponse queryResponse = null;
        try {
            queryResponse = solrClient.query("my_core", solrQuery);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long total = queryResponse.getResults().getNumFound();
        //获取普通结果
        List<Wenshu> wenshuList = queryResponse.getBeans(Wenshu.class);
        JSONObject  jsonObject = new JSONObject();
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(wenshuList));
        jsonObject.put("wenshulist",jsonArray);
        jsonObject.put("total",total);
        return  jsonObject;
    }

    public JSONObject getWenshuList(String condition,int curPage,int pageSize){
        System.out.println(condition);
        SolrQuery solrQuery = new SolrQuery();
        String[] conditions = condition.split(",");
        String content="";
        StringBuffer str = new StringBuffer();
        Integer flag=0;
        if(condition.equals("")){
            content="*";
        }else{
            for(String cond:conditions){

                String type = cond.split(":")[0];
                System.out.println(type);
                if(type.equals("s8")){
                    str.append(" AND anjianType:"+cond.split(":")[1]);
                }else if(type.equals("s2")){
                    str.append(" AND court:"+cond.split(":")[1]);
                }else if(type.equals("s42")){
                    str.append(" AND date:"+cond.split(":")[1]);
                }else if(type.equals("s7")){
                    str.append(" AND num:"+cond.split(":")[1]);
                }else if(type.equals("s1")){
                    str.append(" AND title:"+cond.split(":")[1]);
                }else if(type.equals("s33")){
                    str.append(" AND court:"+cond.split(":")[1]);
                }else if(type.equals("s4")){
                    str.append(" AND court:"+cond.split(":")[1].substring(0, 2));
                }else if(type.equals("s45")){
                    str.append(" AND reason:"+cond.split(":")[1]);
                    flag=1;
                }else if(type.equals("s6")){
                    str.append(" AND title:"+cond.split(":")[1]);
                }else if(type.equals("s21")){
                    str.append(" AND reason:"+cond.split(":")[1]);
                    flag=1;
                }
                else{
                    content+=cond.split(":")[1];
                }
            }
        }

        if(content.equals("")){
            content="*";
        }


        solrQuery.setQuery("file:"+content+str.toString());
        System.out.println("file:"+content+str.toString());
        Integer start = (curPage-1)*pageSize;
        solrQuery.setStart(start);
        solrQuery.setRows(pageSize);
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("reason");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");

        try {
            QueryResponse queryResponse = solrClient.query("my_core", solrQuery);

            long total = queryResponse.getResults().getNumFound();
            //获取普通结果
            List<Wenshu> wenshuList = queryResponse.getBeans(Wenshu.class);
            //获取高亮结果
            if(flag==1){
                Map<String, Map<String,List<String>>> highlighting = queryResponse.getHighlighting();
                System.out.println(highlighting);

                for(Wenshu wenshu:wenshuList){
                    String reason_high = highlighting.get(wenshu.getId()).get("reason").get(0);
                    wenshu.setReason(reason_high);
                }
            }
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(wenshuList));
            JSONObject  jsonObject = new JSONObject();
            jsonObject.put("wenshulist",jsonArray);
            jsonObject.put("total",total);
            System.out.println(jsonArray);
            return jsonObject;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }



    public Wenshu getWenshuById(String id) {

      //  solrClient.setConnectionTimeout(50000);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("court:"+id);

        solrQuery.setStart(0);
        solrQuery.setRows(5);
        System.out.println(solrQuery);
        try {
            QueryResponse document = solrClient.query("myIKCore", solrQuery);
            System.out.println(document);

        } catch (SolrServerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopSpide(String thread) {
        SpideRecord spideRecord = spideRecordMapper.getRecordByName(thread);
        spideRecord.setStatus(0);
        spideRecordMapper.editRecord(spideRecord);
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];

        currentGroup.enumerate(lstThreads);
        // Logger.info("现有线程数" + noThreads);
        for (int i = 0; i < noThreads; i++) {

            String nm = lstThreads[i].getName();
           // System.out.println(nm+thread);
            //     Logger.info("线程号：" + i + " = " + nm);
            if (nm.equals(thread)) {
                System.out.println(nm + thread);
                lstThreads[i].interrupt();
            }
        }
    }

}
