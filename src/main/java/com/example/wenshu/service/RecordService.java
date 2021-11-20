package com.example.wenshu.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.wenshu.mapper.SpideRecordMapper;
import com.example.wenshu.mapper.WenshuMapper;
import com.example.wenshu.model.SpideRecord;
import com.example.wenshu.model.Wenshu;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.jute.Record;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RecordService {
    @Resource
    private SpideRecordMapper spideRecordMapper;
    @Resource
    private WenshuMapper wenshuMapper;

    @Resource
    private SolrClient solrClient;
    public JSONObject getAllRecord(int curPage,int pageSize,String node){
        JSONArray jsonArray=new JSONArray();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//日期指定格式输出
        Page page = PageHelper.startPage(curPage, pageSize);
        List<SpideRecord> records= spideRecordMapper.selectRecord(node);
        for(SpideRecord spideRecord:records){
     //       System.out.println(spideRecord.getStatus());
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id",spideRecord.getId());
                jsonObject1.put("startTime",simpleDateFormat.format(spideRecord.getStartTime()));

                jsonObject1.put("endTime",spideRecord.getEndTime()==null?"":simpleDateFormat.format(spideRecord.getEndTime()));
                jsonObject1.put("wenshuNum",spideRecord.getWenshuNum());
                jsonObject1.put("status",spideRecord.getStatus());
                jsonObject1.put("conditions",spideRecord.getCondition());
                jsonObject1.put("spiderName",spideRecord.getSpiderName());
                jsonObject1.put("progress",spideRecord.getProgress());
                jsonObject1.put("node",spideRecord.getNode());
                jsonObject1.put("skip",spideRecord.getSkip());

                jsonObject1.put("anjianType",wenshuMapper.findNameById(spideRecord.getAnjianType()));
                jsonArray.add(jsonObject1);



        }
        PageInfo<SpideRecord> pageInfo = new PageInfo<>(records);
        long total = pageInfo.getTotal();;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",total);
        jsonObject.put("recordlist",jsonArray);
        return jsonObject;
    }

    public JSONObject getLine(){

        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for(int i = 7;i>=0;i--){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, - i);
            Date monday = c.getTime();
            String preMonday = sdf.format(monday);
            int num = wenshuMapper.getNumByDate(preMonday);
            jsonArray1.add(preMonday);
            jsonArray2.add(num);


            System.out.println(preMonday);

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("datelist",jsonArray1);
        jsonObject.put("numlist",jsonArray2);
        return jsonObject;
    }
    public JSONObject getBar(){
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        String[] levels = new String[]{"最高","高级","中级","基层"};
        for(String level:levels){

            int num = wenshuMapper.getNumByLevel(level);
            jsonArray1.add(level);
            jsonArray2.add(num);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("levellist",jsonArray1);
        jsonObject.put("numlist",jsonArray2);
        return jsonObject;
    }

    public JSONObject getMap(){
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        String[] citys = new String[]{"北京","天津","河北","山西","内蒙古","辽宁","吉林","黑龙江","上海","江苏","浙江","安徽",
        "福建","江西","山东","河南","湖北","湖南","广东","广西","海南","重庆","四川",
        "贵州","云南","西藏","陕西","甘肃","青海","宁夏","新疆"};
        for(String city:citys){

            JSONObject jsonObject1 = new JSONObject();
           // int num = wenshuMapper.getNumByCity(city);

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery("court:"+city);
            QueryResponse queryResponse = null;
            try {
                queryResponse = solrClient.query("my_core", solrQuery);
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            long num = queryResponse.getResults().getNumFound();

         //   List<Wenshu> wenshuList = queryResponse.getBeans(Wenshu.class);
            jsonObject1.put("name",city);
            jsonObject1.put("value",num);
            jsonArray1.add(jsonObject1);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("retlist",jsonArray1);
        return jsonObject;
    }

    public int removeSpider(String thread) {

        Integer status = spideRecordMapper.getStatus(thread);
        if(status==3){
            spideRecordMapper.deleteSpider(thread);
        }
       return spideRecordMapper.removeSpider(thread);
    }

    public int getSpidingNum(){
        return spideRecordMapper.getSpidingNum();
    }


    public JSONObject getSpiderByDate(){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<SpideRecord> records= spideRecordMapper.getSpiderByDate(sdf.format(now));
        JSONArray jsonArray=new JSONArray();
        for(SpideRecord spideRecord:records){
            float v = 0;
            //  System.out.println(spideRecord.getStatus());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",spideRecord.getId());
            jsonObject1.put("startTime",spideRecord.getStartTime());
            jsonObject1.put("endTime",spideRecord.getEndTime()==null?"":spideRecord.getEndTime());
            jsonObject1.put("wenshuNum",spideRecord.getWenshuNum());
            jsonObject1.put("skip",spideRecord.getSkip());
            jsonObject1.put("status",spideRecord.getStatus());
            jsonObject1.put("conditions",spideRecord.getCondition());
            jsonObject1.put("spiderName",spideRecord.getSpiderName());
            jsonObject1.put("anjianType",wenshuMapper.findNameById(spideRecord.getAnjianType()));
            System.out.println(spideRecord.getWenshuNum());

            jsonObject1.put("node",spideRecord.getNode());
            jsonArray.add(jsonObject1);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("num",jsonArray.size());
        jsonObject.put("recordlist",jsonArray);
        return jsonObject;

    }


    public JSONObject getFinishRecord(Integer curPage, Integer pageSize) {
        JSONArray jsonArray=new JSONArray();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//日期指定格式输出
        Page page = PageHelper.startPage(curPage, pageSize);
        List<SpideRecord> records= spideRecordMapper.getFinishRecord();
        for(SpideRecord spideRecord:records){
            float v = 0;
          //  System.out.println(spideRecord.getStatus());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",spideRecord.getId());
            jsonObject1.put("startTime",spideRecord.getStartTime());

            jsonObject1.put("endTime",spideRecord.getEndTime()==null?"":spideRecord.getEndTime());
            jsonObject1.put("wenshuNum",spideRecord.getWenshuNum());
            jsonObject1.put("status",spideRecord.getStatus());
            jsonObject1.put("conditions",spideRecord.getCondition());
            jsonObject1.put("spiderName",spideRecord.getSpiderName());
            jsonObject1.put("skip",spideRecord.getSkip());
            jsonObject1.put("anjianType",wenshuMapper.findNameById(spideRecord.getAnjianType()));
            System.out.println(spideRecord.getWenshuNum());
            v =(float) spideRecord.getWenshuNum()/((float)(spideRecord.getEndTime().getTime()-spideRecord.getStartTime().getTime())/1000);
            jsonObject1.put("speed",v);
            jsonObject1.put("node",spideRecord.getNode());
            jsonArray.add(jsonObject1);
        }
        PageInfo<SpideRecord> pageInfo = new PageInfo<>(records);
        long total = pageInfo.getTotal();;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",total);
        jsonObject.put("recordlist",jsonArray);
        return jsonObject;
    }
}
