package org.example.carpooling.Service;

import org.example.carpooling.Entity.LoginForm;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {

    int login(LoginForm loginForm);
}
