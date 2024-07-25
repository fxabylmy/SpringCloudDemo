package com.example.orderservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.model.order.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
