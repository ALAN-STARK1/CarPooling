package org.example.carpooling.service.LoginServiceImpl;

import cn.hutool.core.util.IdUtil;
import org.example.carpooling.constants.RedisConstants;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.UserDTO;
import org.example.carpooling.entity.LoginForm;
import org.example.carpooling.entity.User;
import org.example.carpooling.mapper.UserMapper;
import org.example.carpooling.service.LoginService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result register(LoginForm loginForm) {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();

        if(username == null || password == null
                || username.isEmpty() || password.isEmpty()
        ){
            return Result.error("用户名或密码不能为空");
        }
        long id = IdUtil.getSnowflakeNextId();

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");
        user.setAvatar("src/main/resources/picture/证件照.jpg");
        user.setStatus(1);


        userMapper.insert(user);

        return Result.ok("注册成功");

    }

    @Override
    public Result login(LoginForm loginForm){
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();

        if(username == null || password == null
        || username.isEmpty() || password.isEmpty()
        ){
            return Result.error("用户名或密码不能为空");
        }

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(loginForm,userDTO);
        User user = userMapper.login(userDTO);
        if(user == null){
            return Result.error("用户名或密码错误");
        }
        else{
            BeanUtils.copyProperties(user,userDTO);
        }

        String token = UUID.randomUUID().toString();
        String key = RedisConstants.LOGIN_USER_KEY + token;

        //存入redis
        Map<String,String> userMap = new HashMap<>();
        userMap.put("id",String.valueOf(userDTO.getId()));
        userMap.put("username",String.valueOf(userDTO.getUsername()));
        userMap.put("avatar",String.valueOf(userDTO.getAvatar()));

        stringRedisTemplate.opsForHash().putAll(key, userMap);

        HashMap<String,Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userDTO);

        return Result.ok(data);

    }


}
