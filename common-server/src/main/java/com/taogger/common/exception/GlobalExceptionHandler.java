package com.taogger.common.exception;

import com.taogger.common.utils.ServerJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验错误异常
     * @param e BindException
     * @return YXJJSONResult
     */
    @ExceptionHandler(value = {BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerJSONResult bindExceptionHandler(BindException e) {
        log.error("参数校验异常,异常信息,e:{}",e);
        return ServerJSONResult.paramsErrorMsg(e.getFieldError().getDefaultMessage());
    }

    /**
     * 参数校验错误异常 MethodArgumentNotValidException
     * @param e MethodArgumentNotValidException
     * @return YXJJSONResult
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ServerJSONResult MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("参数校验异常,异常信息,e:{}",e);
        return ServerJSONResult.paramsErrorMsg(e.getMessage());
    }

    /**
     * 捕获自定义的统一全局异常
     * @param e ApiException
     * @return CommonResult
     */
    @ExceptionHandler(value = ApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerJSONResult handle(ApiException e) {
        log.error("业务处理异常,异常信息为:{}",e);
        return ServerJSONResult.errorDisposeMsg(e.getMessage());
    }

    /**
     * 捕获未知异常
     * @param e Exception
     * @return CommonResult
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerJSONResult commonExceptionHandler(Exception e) {
        log.error("服务出现异常,异常信息为: {}", e);
        return ServerJSONResult.errorMsg("系统异常,请稍后重试");
    }
}
