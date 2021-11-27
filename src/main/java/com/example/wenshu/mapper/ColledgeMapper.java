package com.example.wenshu.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ColledgeMapper {
    Integer selectByCollegeName(String collegeName);
}
