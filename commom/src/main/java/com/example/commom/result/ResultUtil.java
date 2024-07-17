package com.example.commom.result;

import static com.example.commom.result.ErrorCode.SUCCESS;

/**
 * 返回结果Util
 *
 * @author fxab
 * @date 2024/07/17
 */
public class ResultUtil {

    /**
     * 成功
     *
     * @param date 日期
     * @return {@link BaseResult}<{@link T}>
     */
    public static <T> BaseResult<T> success(T date) {
        return new BaseResult<>(SUCCESS.getCode(),date, SUCCESS.getMessage());
    }

    /**
     * 成功
     *
     * @return {@link BaseResult}<{@link T}>
     */
    public static <T> BaseResult<T> success() {
        return new BaseResult<>(SUCCESS.getCode(),SUCCESS.getMessage());
    }

    /**
     * 错误
     *
     * @param code    代码
     * @param message 信息
     * @return {@link BaseResult}<{@link T}>
     */
    public static <T> BaseResult<T> error(int code,String message) {
        return new BaseResult<>(code,message);
    }

    public static <T> BaseResult<T> error(ErrorCode errorCode,String message) {
        return new BaseResult<>(errorCode.getCode(),message);
    }

    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @return {@link BaseResult}<{@link T}>
     */
    public static <T> BaseResult<T> error(ErrorCode errorCode) {
        return new BaseResult<>(errorCode.getCode(),errorCode.getMessage());
    }
}
