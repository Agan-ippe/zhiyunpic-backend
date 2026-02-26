package com.zhimo.zhiyunpic.model.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026/02/25   21:31
 * @Description 用户响应封装类
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = -943188976289700779L;

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

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
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;
}
