package com.example.commom.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BaseResult<T> implements Serializable {

    private int code;

    private T date;

    private String message;

    public BaseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResult(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
