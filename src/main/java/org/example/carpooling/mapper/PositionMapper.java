package org.example.carpooling.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.carpooling.entity.Position;

@Mapper
public interface PositionMapper {

    @Insert("insert into  position (id,name,city,lng,lat) values (#{id},#{name},#{city},#{lng},#{lat}) ")
    void insert(Position position);

}
