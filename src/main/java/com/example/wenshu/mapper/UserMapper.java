package com.example.wenshu.mapper;



import com.example.wenshu.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserMapper {
    User selectByPrimaryKey(int userid);

    int addUser(User user);
}