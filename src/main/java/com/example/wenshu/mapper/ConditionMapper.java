package com.example.wenshu.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConditionMapper {
    String findXuhaoByName(String name);
    String findNameByXuhao(String xuhao);
}
