package org.example.carpooling.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String sex;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String avatar;
    private int status;
    private String role;

}
