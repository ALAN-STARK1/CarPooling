package org.example.carpooling.service.serviceImpl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;
import org.example.carpooling.entity.Order;
import org.example.carpooling.mapper.PositionMapper;
import org.example.carpooling.mapper.RouterMapper;
import org.example.carpooling.service.RouterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.carpooling.entity.Position;

import java.util.UUID;

@Slf4j
@Service
public class RouterServiceImpl implements RouterService {

    @Autowired
    private RouterMapper routerMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Override
    public Result submitOrder(SubmitOrderRequest submitOrderRequest) {

        long userid = IdUtil.getSnowflakeNextId();

        long boardingId = IdUtil.getSnowflakeNextId();

        long dropOffId = IdUtil.getSnowflakeNextId();

        Position boardingPosition = new Position();
        Position dropOffPosition = new Position();
        BeanUtils.copyProperties(submitOrderRequest.getBoardingExact(), boardingPosition);
        BeanUtils.copyProperties(submitOrderRequest.getDropOffExact(), dropOffPosition);

        boardingPosition.setId(boardingId);
        dropOffPosition.setId(dropOffId);

        positionMapper.insert(boardingPosition);
        positionMapper.insert(dropOffPosition);
        //todo aop获取登录信息

        //todo提交完成做订单

       return Result.ok(routerMapper.submitOrder(submitOrderRequest));
    }





}
