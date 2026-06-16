package com.zzyl.exception;

import cn.hutool.core.util.ObjectUtil;
import com.zzyl.base.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义异常BaseException。
     * 返回自定义异常中的错误代码和错误消息。
     *
     * @param exception 自定义异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(BaseException.class)
    public ResponseResult<Object> handleBaseException(BaseException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getBasicEnum())) {
            log.error("自定义异常处理:{}", exception.getBasicEnum().getMsg());
        }

        return ResponseResult.error(exception.getBasicEnum());

    }

    /**
     * 处理参数校验异常（MethodArgumentNotValidException）
     * 处理@Validated注解的参数校验失败异常
     *
     * @param exception 参数校验异常
     * @return 响应数据，包含错误字段和错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("参数校验失败: {}", errorMessage);
        return ResponseResult.error(400, "参数校验失败: " + errorMessage);
    }

    /**
     * 处理参数绑定异常（BindException）
     * 处理表单数据绑定失败异常
     *
     * @param exception 参数绑定异常
     * @return 响应数据，包含错误信息
     */
    @ExceptionHandler(BindException.class)
    public ResponseResult<Object> handleBindException(BindException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数绑定失败: {}", errorMessage);
        return ResponseResult.error(400, "参数绑定失败: " + errorMessage);
    }

    /**
     * 处理约束违反异常（ConstraintViolationException）
     * 处理@Validated注解的方法参数校验失败异常
     *
     * @param exception 约束违反异常
     * @return 响应数据，包含错误信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数约束违反: {}", errorMessage);
        return ResponseResult.error(400, "参数约束违反: " + errorMessage);
    }

    /**
     * 处理HTTP消息不可读异常
     * 处理JSON解析失败等请求体格式错误
     *
     * @param exception HTTP消息不可读异常
     * @return 响应数据，包含错误信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("请求体格式错误: {}", exception.getMessage());
        return ResponseResult.error(400, "请求体格式错误，请检查JSON格式");
    }

    /**
     * 处理运行时异常
     * 处理业务逻辑中的RuntimeException
     *
     * @param exception 运行时异常
     * @return 响应数据，包含错误信息
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<Object> handleRuntimeException(RuntimeException exception) {
        log.error("运行时异常: {}", exception.getMessage(), exception);
        return ResponseResult.error(500, "系统运行异常: " + exception.getMessage());
    }

    /**
     * 处理其他未知异常。
     * 返回HTTP响应状态码500，包含错误代码和异常堆栈信息。
     *
     * @param exception 未知异常
     * @return 响应数据，包含错误代码和异常堆栈信息
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> handleUnknownException(Exception exception) {
        log.error("未知异常: {}", exception.getMessage(), exception);
        return ResponseResult.error(500, "系统内部错误");
    }

}
