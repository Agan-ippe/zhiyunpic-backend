package com.zhimo.zhiyunpic.exception;

import lombok.Getter;

/**
 * @Author <a href="https://github.com/Agan-ippe">知莫</a>
 * @Date 2026/02/23   14:00
 * @Version 1.0
 * @Description 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}

