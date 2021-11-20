package com.example.wenshu.mapper;

import com.example.wenshu.model.SpideRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SpideRecordMapper {
    int addRecord(SpideRecord spideRecord);
    List<SpideRecord> selectRecord(String node);
    List<SpideRecord> getFinishRecord();
    List<SpideRecord> getSpiderByDate(String today);
    int updateNum(Integer num,String SpiderName);

    int editRecord(SpideRecord spideRecord);
    SpideRecord getRecordByName(String thread);
    int getSpidingNum();
    int removeSpider(String thread);
    int getStatus(String spiderName);
    int deleteSpider(String spiderName);


}
