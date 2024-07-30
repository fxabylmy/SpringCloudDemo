package com.example.common.exception;

import com.example.common.result.BaseResult;
import com.example.common.result.ErrorCode;
import com.example.common.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLException;

/**
 * 全局异常处理器
 *
 * @author fxab
 * @date 2024/07/17
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     *
     * @param e e
     * @return {@link BaseResult}<{@link ?}>
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResult<?> businessExceptionHandler(BusinessException e) {
        log.error("自定义异常：", e);
        return ResultUtil.error(e.getCode(), e.getMessage());
    }


    /**
     * SQL异常处理
     *
     * @param e e
     * @return {@link BaseResult}<{@link ?}>
     */
    @ExceptionHandler(SQLException.class)
    public BaseResult<?> sqlExceptionHandler(SQLException e) {
        log.error("SQLException", e);
        return ResultUtil.error(ErrorCode.DATABASE_ERROR, "数据库操作错误");
    }


    /**
     * 处理运行时异常
     *
     * @param e e
     * @return {@link BaseResult}<{@link ?}>
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResult<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("运行时异常：", e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
