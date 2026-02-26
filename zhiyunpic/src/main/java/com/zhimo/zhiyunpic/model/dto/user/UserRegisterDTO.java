package com.zhimo.zhiyunpic.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026/02/25   21:13
 * @Description 用户注册请求封装类
 */
@Data
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = 9128427429084128975L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 密码校验
     */
    private String checkPassword;
}
