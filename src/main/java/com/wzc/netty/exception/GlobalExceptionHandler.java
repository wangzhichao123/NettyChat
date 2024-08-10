package com.wzc.netty.exception;

import com.wzc.netty.enums.StatusCodeEnum;
import com.wzc.netty.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * validation参数校验异常
     * 注：这里是针对 @RequestBody 注解
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> exceptionHandler(MethodArgumentNotValidException e) {
        /**
         * e.getMessage() 是全路径错误信息
         */
        Map<String, String> errorMap = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.merge(
                        error.getField(),
                        error.getDefaultMessage(),
                        (existing, newError) -> existing + ", " + newError
                );
            });
        }
        log.info("validation parameters error！{}", errorMap);
        return R.fail(StatusCodeEnum.VALID_ERROR, errorMap);
    }

    /**
     * validation参数校验异常
     * 注：这里是针对 @ModelAttribute 或 @RequestParam 注解
     */
    @ExceptionHandler(value = BindException.class)
    public R<?> exceptionHandler(BindException e) {
        Map<String, String> errorMap = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errorMap.merge(
                        error.getField(),
                        error.getDefaultMessage(),
                        (existing, newError) -> existing + ", " + newError
                );
            });
        }
        log.info("validation parameters error！{}", errorMap);
        return R.fail(StatusCodeEnum.VALID_ERROR, errorMap);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(value = BizException.class)
    public R<?> exceptionHandler(BizException e) {
        return R.fail(e.getCode(), e.getMessage());
    }
}
