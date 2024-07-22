package com.example.commom.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author fxab
 * @date 2024/07/22
 */
@Data
@AllArgsConstructor
public class BaseResult<T> implements Serializable {

    /**
     * 代码
     */
    private int code;

    /**
     * 日期
     */
    private T date;

    /**
     * 信息
     */
    private String message;

    /**
     * 基本结果
     *
     * @param code    代码
     * @param message 信息
     */
    public BaseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 基本结果
     *
     * @param errorCode 错误代码
     */
    public BaseResult(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
