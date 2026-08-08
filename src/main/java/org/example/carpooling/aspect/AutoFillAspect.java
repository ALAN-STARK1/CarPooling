package org.example.carpooling.aspect;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Console;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.carpooling.DTO.UserDTO;
import org.example.carpooling.annotation.AutoFill;
import org.example.carpooling.constants.AutoFillConstant;
import org.example.carpooling.enumeration.OperationType;
import org.example.carpooling.util.UserHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* org.example.carpooling.mapper.*.*(..)) " +
            "&& @annotation(org.example.carpooling.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        Console.log("开始公共字段自动填充..");

        MethodSignature signature =  (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity = args[0];
        DateTime now =  DateTime.now();
        UserDTO user  = UserHolder.getUser();
        Long currentId = user == null ? null : user.getId();

        try{
            if(operationType == OperationType.INSERT){
                invokeIfExists(entity, AutoFillConstant.SET_CREATE_TIME,DateTime.class,now);
                invokeIfExists(entity,AutoFillConstant.SET_UPDATE_TIME,DateTime.class,now);
            }
            else if(operationType == OperationType.UPDATE){
                invokeIfExists(entity,AutoFillConstant.SET_UPDATE_TIME,DateTime.class,now);
            } else if(operationType == OperationType.FINISH){
                invokeIfExists(entity,AutoFillConstant.SET_END_TIME,DateTime.class,now);
                invokeIfExists(entity,AutoFillConstant.SET_UPDATE_TIME,DateTime.class,now);
            }

        } catch (Exception e) {
            throw new RuntimeException("自动填充失败",e);
        }


    }

    private void invokeIfExists(Object entity,String methodName,Class <?> paramType,Object value) throws Exception {
        if(value == null){
            return;
        }
        try{
            Method method = entity.getClass().getMethod(methodName,paramType);
            method.invoke(entity,value);
        } catch (NoSuchMethodException ignore) {

        }
    }

}
