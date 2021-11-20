package com.example.wenshu.controller;

import com.example.wenshu.service.RecordService;
import com.example.wenshu.service.WenshuService;
import com.example.wenshu.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecordController {
    @Autowired
    private RecordService recordService;

    @Autowired
    private WenshuService wenshuService;
    @GetMapping("/getRecords")
    public Result getRecords(  @RequestParam(value ="pageSize",defaultValue = "-1") Integer pageSize,
                               @RequestParam(value ="curPage",defaultValue = "-1") Integer curPage,
                                     @RequestParam(value ="node",defaultValue = "") String node){
        if(curPage==-1||pageSize==-1){
            return Result.build(400,"传参失败");
        }else{
            return Result.build(200,"",recordService.getAllRecord(curPage,pageSize,node));
        }
    }

    @GetMapping("/getFinishRecord")
    public Result getFinishRecord(@RequestParam(value ="pageSize",defaultValue = "-1") Integer pageSize,
                                  @RequestParam(value ="curPage",defaultValue = "-1") Integer curPage){
        if(curPage==-1||pageSize==-1){
            return Result.build(400,"传参失败");
        }else{
            return Result.build(200,"",recordService.getFinishRecord(curPage,pageSize));
        }
    }

    @GetMapping("/removeSpider")
    public  Result removeSpider( @RequestParam(value ="thread",defaultValue = "-1") String thread){
        return Result.build(200,"",recordService.removeSpider(thread));

    }



    @GetMapping("/getLine")
    public Result getLine(){
        recordService.getLine();

        return Result.build(200,"",recordService.getLine());
    }

    @GetMapping("/getBar")
    public Result getBar(){
        recordService.getBar();

        return Result.build(200,"",recordService.getBar());
    }
    @GetMapping("/getSpidingNum")
    public Result getSpidingNum(){


        return Result.build(200,"",recordService.getSpidingNum());
    }
    @GetMapping("/getMap")
    public Result getMap(){
        recordService.getMap();

        return Result.build(200,"",recordService.getMap());
    }

    @GetMapping("/getSpiderByDate")
        public Result getSpiderByDate(){


        return Result.build(200,"",recordService.getSpiderByDate());
    }
}
