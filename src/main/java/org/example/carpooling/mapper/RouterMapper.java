package org.example.carpooling.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.SubmitOrderRequest;

@Mapper
public interface RouterMapper {
    Result submitOrder(SubmitOrderRequest submitOrderRequest);
}
