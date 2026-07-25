package org.example.carpooling.controller;

import org.example.carpooling.DTO.Result;
import org.example.carpooling.entity.LoginForm;
import org.example.carpooling.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/user/register")
    public Result register(@RequestBody LoginForm loginForm) {
        return loginService.register(loginForm);
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody LoginForm loginForm) {
        return loginService.login(loginForm);
    }
}
