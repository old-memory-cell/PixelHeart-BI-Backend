package com.zzsd.inolibi.exception;

import com.zzsd.inolibi.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 业务代码中直接抛出 RuntimeException 时，统一转换为 Result 结构返回，
 * 避免前端只拿到无响应体的 500、无法展示具体失败原因。
 *
 * @author inoli
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e 运行时异常
     * @return 统一失败结果
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 兜底异常处理
     *
     * @param e 异常
     * @return 统一失败结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }

}
