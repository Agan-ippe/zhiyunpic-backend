package com.zhimo.zhiyunpic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhimo.zhiyunpic.model.dto.user.UserQueryDTO;
import com.zhimo.zhiyunpic.model.dto.user.UserUpdateDTO;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.user.UserLoginVO;
import com.zhimo.zhiyunpic.model.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 93988
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-02-25 20:59:10
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param request
     * @return 脱敏后的用户响应封装类
     */
    UserLoginVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    boolean userLogout(HttpServletRequest request);

    //    endregion
    //    region 增删改查
    /**
     * 更新用户信息
     * @param updateDTO 更新信息封装类
     * @return
     */
    boolean updateUser(UserUpdateDTO updateDTO);

    /**
     * 分页查询用户
     * @param queryDTO
     * @return
     */
    List<UserVO> listUsers(UserQueryDTO queryDTO);



}
