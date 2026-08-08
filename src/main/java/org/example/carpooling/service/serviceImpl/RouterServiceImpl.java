package org.example.carpooling.service.serviceImpl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;
import org.example.carpooling.DTO.UserDTO;
import org.example.carpooling.entity.Order;
import org.example.carpooling.entity.Position;
import org.example.carpooling.mapper.OrderMapper;
import org.example.carpooling.mapper.PositionMapper;
import org.example.carpooling.service.RouterService;
import org.example.carpooling.util.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouterServiceImpl implements RouterService {

    @Autowired
    private PositionMapper positionMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取订单请求信息：生成 id，插入 position，再插入 order。
     */
    @Override
    public Result submitOrder(SubmitOrderRequest submitOrderRequest) {
        UserDTO userDTO = UserHolder.getUser();
        if (userDTO == null || userDTO.getId() == null) {
            return Result.error("未登录");
        }
        if (submitOrderRequest.getBoardingExact() == null || submitOrderRequest.getDropOffExact() == null) {
            return Result.error("起点和终点不能为空");
        }

        long id = IdUtil.getSnowflakeNextId();
        long orderId = IdUtil.getSnowflakeNextId();
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

        Order order = new Order();
        order.setId(id);
        order.setBoardingPositionId(boardingId);
        order.setDropOffPositionId(dropOffId);

        String type = submitOrderRequest.getSubmitType();
        // 前端乘客页传「乘客」，兼容 Driver/司机
        if ("Driver".equalsIgnoreCase(type) || "司机".equals(type)) {
            order.setDriverId(userDTO.getId());
            order.setOrderId(orderId);
        } else {
            order.setPassengerId(userDTO.getId());
        }

        // createTime / updateTime 由 OrderMapper.@AutoFill 填充
        orderMapper.insert(order);

        return Result.ok("订单提交成功");
    }
}
