package org.example.carpooling.Controller;

import org.example.carpooling.Entity.LoginForm;
import org.example.carpooling.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;


    @PostMapping("user/login")
    public int login(@RequestBody LoginForm loginForm) {
        return loginService.login(loginForm);
    }



}
