package org.example.carpooling.entity;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.util.Date;

@Data
public  class Order {

    Long id;
    Long orderId;
    Long boardingPositionId;
    Long dropOffPositionId;
    Long passengerId;
    Long driverId;
    DateTime createTime;
    DateTime updateTime;
    DateTime endTime;
    double passengerCost;
    double driverIncome;


}
