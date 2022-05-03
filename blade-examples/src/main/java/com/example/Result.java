package com.example;

import lombok.Data;

@Data
public class Result<T> {

    private int code = 0;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.data = data;
        return result;
    }

}
