package org.example.carpooling.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.carpooling.annotation.AutoFill;
import org.example.carpooling.entity.Order;
import org.example.carpooling.enumeration.OperationType;

@Mapper
public interface OrderMapper {

    @AutoFill(OperationType.INSERT)
    public void insert(Order order);

}
