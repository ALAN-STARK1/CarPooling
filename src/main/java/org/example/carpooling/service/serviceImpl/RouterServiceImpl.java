package org.example.carpooling.service.serviceImpl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;
import org.example.carpooling.entity.Order;
import org.example.carpooling.mapper.RouterMapper;
import org.example.carpooling.service.RouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class RouterServiceImpl implements RouterService {

    @Autowired
    private RouterMapper routerMapper;

    @Override
    public Result submitOrder(SubmitOrderRequest submitOrderRequest) {

        long id = IdUtil.getSnowflakeNextId();
        Order order = new Order();
       return Result.ok(routerMapper.submitOrder(submitOrderRequest));
    }
}
