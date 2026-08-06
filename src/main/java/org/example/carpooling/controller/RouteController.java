package org.example.carpooling.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;
import org.example.carpooling.service.RouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 路线 / 订单相关接口。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RouteController {

    @Autowired
    private final RouterService routerService;

    /**
     * 提交起终点订单。
     * POST /route/submitOrder
     * body: { boardingExact: {...}, dropOffExact: {...} }
     */
    @PostMapping("/route/submitOrder")
    public Result submitOrder(@RequestBody SubmitOrderRequest request) {
        return routerService.submitOrder(request);
    }
}
