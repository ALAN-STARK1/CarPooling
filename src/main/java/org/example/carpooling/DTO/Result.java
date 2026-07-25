package org.example.carpooling.DTO;

import lombok.Data;

@Data
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;

    public static Result ok(Object data) {
        Result result = new Result();
        result.success = true;
        result.data = data;
        return result;
    }

    public static Result error(String errorMsg) {
        Result result = new Result();
        result.success = false;
        result.errorMsg = errorMsg;
        return result;
    }
}
