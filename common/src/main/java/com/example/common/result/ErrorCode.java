package com.example.common.result;

/**
 * 自定义错误码
 *
 * @author fxab
 * @date 2024/07/17
 */
public enum ErrorCode {


    SUCCESS(200,"成功"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    PARAMS_ERROR(50010,"参数错误"),
    DATABASE_ERROR(50020, "数据库操作失败"),
    LOGOUT_ERROR(50030,"登出失败"),
    TOKEN_MISSION(40010,"Token丢失"),
    TOKEN_INVALID(40020,"Token无效");
    ;

    /**
     * 错误码
     */
    private final int code;
    /**
     * 信息
     */
    private final String message;

    /**
     * 错误信息
     *
     * @param code    错误码
     * @param message 信息
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return int
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取消息
     *
     * @return {@link String}
     */
    public String getMessage() {
        return message;
    }
}
