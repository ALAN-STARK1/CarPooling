package org.example.carpooling.Service.LoginServiceImpl;

import org.example.carpooling.Entity.LoginForm;
import org.example.carpooling.Service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class LoginServiceImpl implements LoginService {
    @GetMapping("/users")
    @Override
    public int login(LoginForm loginForm){
        System.out.println(loginForm.toString());
        return 0;
    }


}
