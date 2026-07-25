package org.example.carpooling.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.carpooling.DTO.UserDTO;
import org.example.carpooling.entity.User;

@Mapper
public interface UserMapper {

    void insert(User user);

    User getUser(int id);

    User login(UserDTO userDTO);
}
