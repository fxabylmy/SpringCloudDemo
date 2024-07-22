package com.example.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.model.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
