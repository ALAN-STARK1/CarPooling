package org.example.carpooling.util;

import lombok.Data;
import org.example.carpooling.DTO.UserDTO;

@Data
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user) {tl.set(user);}

    public static UserDTO getUser() {
        return tl.get();
    }

    public static void removeUser() {tl.remove();}
}
