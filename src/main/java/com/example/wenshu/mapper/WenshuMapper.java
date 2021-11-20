package com.example.wenshu.mapper;

import com.example.wenshu.model.Wenshu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WenshuMapper {
    int insert(Wenshu wenshu);
    List<Wenshu> selectByType(Wenshu wenshu);
    Integer getselectNum(Wenshu wenshu);
    Integer numByType(String anjianType);
    String findNameById(String Id);
    String getWenshuHtml(String Id);
    Integer getNumByDate(String date);
    Integer getNumByLevel(String level);
    Long   selectByType_COUNT();

    int getNumByCity(String city);
    List<Wenshu> findWenshuBySpider(String spiderName);
    // void insert(Wenshu wenshu);
}
