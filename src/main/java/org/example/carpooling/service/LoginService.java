package org.example.carpooling.service;

import org.example.carpooling.DTO.Result;
import org.example.carpooling.entity.LoginForm;
import org.springframework.stereotype.Service;

import java.rmi.registry.Registry;

@Service
public interface LoginService {

    Result register(LoginForm loginForm);

    Result login(LoginForm loginForm);
}
