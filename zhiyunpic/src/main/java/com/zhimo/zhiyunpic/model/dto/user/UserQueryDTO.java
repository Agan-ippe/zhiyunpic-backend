package com.zhimo.zhiyunpic.model.dto.user;

import com.zhimo.zhiyunpic.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026/02/25   21:13
 * @Description 用户查询请求封装类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageRequest implements Serializable {

    private static final long serialVersionUID = 9128427429084128977L;

    /**
     * 用户ID
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
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private String userRole;
}
