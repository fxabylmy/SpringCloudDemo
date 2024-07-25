package com.example.orderservice.controller.inner;

import com.example.model.order.pojo.Order;
import com.example.orderservice.service.OrderService;
import com.example.serviceClient.service.order.OrderFeignClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 测试分布式事务
 * @author fxab
 * @date 2024/07/24
 */
@RestController
@RequestMapping("/inner")
@Slf4j
public class OrderInnerController implements OrderFeignClient {

    /**
     *
     */
    @Resource
    private OrderService orderService;

    /**
     * @param userId
     * @return {@link Boolean}
     */
    @Override
    public Boolean save(Long userId) {
        Order order = Order.builder()
                //.orderId(1L)
                .userId(userId)
                .message("测试分布式事务")
                //.createTime(new Date())
                //.updateTime(new Date())
                .isDelete(Integer.valueOf("0"))
                .build();
        System.out.println(order);
        log.info("开始生成order");
        return orderService.save(order);
    }
}
