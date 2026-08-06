package org.example.carpooling.service.serviceImpl;

import org.example.carpooling.DTO.Result;
import org.example.carpooling.entity.Position;
import org.example.carpooling.mapper.PositionMapper;
import org.example.carpooling.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl implements PositionService {
    @Autowired
    private PositionMapper positionMapper;


    public void insert(Position position){
        positionMapper.insert(position);
    }

}
