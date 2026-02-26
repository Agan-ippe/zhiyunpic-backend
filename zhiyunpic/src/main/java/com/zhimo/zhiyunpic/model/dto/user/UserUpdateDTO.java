package com.zhimo.zhiyunpic.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026/02/25   21:13
 * @Description 用户更新请求封装类
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 9128427429084128978L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;
}
