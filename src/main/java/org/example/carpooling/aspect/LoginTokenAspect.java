package org.example.carpooling.aspect;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.ServletRequestAttributeEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.carpooling.DTO.Result;
import org.example.carpooling.DTO.UserDTO;
import org.example.carpooling.constants.RedisConstants;
import org.example.carpooling.util.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginTokenAspect {
    private final StringRedisTemplate stringRedisTemplate;

    @Around("execution(* org.example.carpooling.controller..*(..)) "
            + "&& !execution(* org.example.carpooling.controller.LoginController.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        try{
            HttpServletRequest req = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();

            String token = req.getHeader("authorization");
            if(StrUtil.isBlank(token)){
                return Result.error("未登录");
            }

            String key = RedisConstants.LOGIN_USER_KEY + ":" + token;
            Map <Object,Object> map =  stringRedisTemplate.opsForHash().entries(key);
            if(map.isEmpty()){
                return Result.error("登录已过期");
            }

            UserDTO user = BeanUtil.fillBeanWithMap(map,new UserDTO(),false);
            UserHolder.saveUser(user);
            stringRedisTemplate.expire(key,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

            return pjp.proceed();

        } finally {
            UserHolder.removeUser();
        }
    }

}
