package com.example.wenshu.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wenshu.model.Wenshu;
import com.example.wenshu.service.WenshuService;

import com.example.wenshu.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WenshuController {
    @Autowired
    private WenshuService wenshuService;
  //  private WenshuSolrService wenshuSolrService;


    @GetMapping("/getWenshuNum")
    public Result getWenshuNum(){

        return Result.build(200,"成功",wenshuService.getNum());
    }

    @GetMapping("/getWenshuHtml")
    public Result getWenshuHtml(@RequestParam(value = "Id",defaultValue = "-1") String Id){
        return Result.build(200,"成功",wenshuService.getHtml(Id));
    }


    @GetMapping("/getWenshuList")
    public Result getWenshuList(@RequestParam(value = "title",defaultValue = "") String title,
                                @RequestParam(value = "anjianType",defaultValue = "") String anjianType,
                                @RequestParam(value = "courtName",defaultValue = "") String courtName,
                                @RequestParam(value = "anjianNum",defaultValue = "") String anjianNum,
                                @RequestParam(value = "file",defaultValue = "") String file,
                                @RequestParam(value ="pageSize",defaultValue = "-1") Integer pageSize,
                                @RequestParam(value ="curPage",defaultValue = "-1") Integer curPage){

        if(curPage==-1||pageSize==-1){
            return Result.build(400,"传参失败");
        }else{
            Wenshu wenshu = new Wenshu();
            wenshu.setTitle(title);
            wenshu.setAnjianType(anjianType);
            wenshu.setCourt(courtName);
            wenshu.setNum(anjianNum);
            wenshu.setFile(file);

            JSONObject jsonObject = wenshuService.getWeneshu(wenshu,curPage,pageSize);

            return Result.build(200,"获取成功",jsonObject);
        }
    }


    @GetMapping("/getCurDate")
    public Result getCurDate(){

        return Result.build(200,"",wenshuService.getCurDateWenshu());
    }

    @GetMapping("/spiderWenshu")
    public Result spiderWenshu(@RequestParam(value = "Cookie",defaultValue = "") String cookie,
                             @RequestParam(value = "userAgent",defaultValue = "") String userAgent,
                              @RequestParam(value="date",defaultValue="") String date,
                               @RequestParam(value = "node",defaultValue = "") String node) throws InterruptedException {
       // String[] conditionlist = conditions.split(",");
        return wenshuService.spiderWenshu(cookie,userAgent,date,node);
//        return wenshuService.spiderWenshu(cookie,userAgent,conditionlist,date,anjianType,node);

    }

    @GetMapping("/getWenshuById")
    public Result getWenshuById(@RequestParam(value = "id",defaultValue = "") String id){
        System.out.println(id);
        return Result.build(200,"",wenshuService.getWenshuById(id));
    }

    @GetMapping("/getWenshu")
    public Result getWenshuList(@RequestParam(value = "condition",defaultValue = "") String condition,
                                @RequestParam(value ="pageSize",defaultValue = "-1") Integer pageSize,
                                @RequestParam(value ="curPage",defaultValue = "-1") Integer curPage){
        return Result.build(200,"",wenshuService.getWenshuList(condition,curPage,pageSize));
    }
    @GetMapping("/findWenshuBySpiderName")
    public Result findWenshuBySpiderName(@RequestParam(value = "spiderName",defaultValue = "") String spiderName,
                                @RequestParam(value ="pageSize",defaultValue = "-1") Integer pageSize,
                                @RequestParam(value ="curPage",defaultValue = "-1") Integer curPage){
        return Result.build(200,"",wenshuService.getWenshuBySpiderName(spiderName,curPage,pageSize));
    }


    @GetMapping("/stopSpide")
    public Result stopSpide(@RequestParam(value = "thread",defaultValue = "")String thread){
        wenshuService.stopSpide(thread);
        return Result.build(200,"爬取已停止");
    }


}
