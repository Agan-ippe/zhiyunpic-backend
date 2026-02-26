package com.zhimo.zhiyunpic.controller;

import com.zhimo.zhiyunpic.common.BaseResponse;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.model.dto.user.UserLoginDTO;
import com.zhimo.zhiyunpic.model.dto.user.UserQueryDTO;
import com.zhimo.zhiyunpic.model.dto.user.UserRegisterDTO;
import com.zhimo.zhiyunpic.model.dto.user.UserUpdateDTO;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.user.UserLoginVO;
import com.zhimo.zhiyunpic.service.UserService;
import com.zhimo.zhiyunpic.service.UserServiceNew;
import com.zhimo.zhiyunpic.utils.ResultUtils;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import com.zhimo.zhiyunpic.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026/02/25   21:40
 * @Description 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceNew userServiceNew;

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        ThrowUtils.throwIf(userRegisterDTO == null,ErrorCode.PARAMS_ERROR);
        long userId = userServiceNew.userRegister(
                userRegisterDTO.getUserAccount(),
                userRegisterDTO.getUserPassword(),
                userRegisterDTO.getCheckPassword()
        );
        return ResultUtils.success(userId);
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO 登录信息
     * @param request      请求
     * @return 登录用户信息
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginDTO == null,ErrorCode.PARAMS_ERROR);
        UserLoginVO userLoginVO = userServiceNew.userLogin(
                userLoginDTO.getUserAccount(),
                userLoginDTO.getUserPassword(),
                request
        );
        return ResultUtils.success(userLoginVO);
    }

    /**
     * 用户注销
     * @param request 请求
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(UserUtils.getUserLoginVO(loginUser));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/get")
    public BaseResponse<UserLoginVO> getUserById(@RequestParam("id") Long id) {
        UserLoginVO UserLoginVO = userServiceNew.getUserById(id);
        return ResultUtils.success(UserLoginVO);
    }

    /**
     * 查询用户列表
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    @GetMapping("/list")
    public BaseResponse<List<UserLoginVO>> listUsers(UserQueryDTO queryDTO) {
        List<UserLoginVO> UserLoginVOList = userServiceNew.listUsers(queryDTO);
        return ResultUtils.success(UserLoginVOList);
    }

    /**
     * 更新用户信息（管理员权限）
     *
     * @param updateDTO 更新信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @RequireAdmin
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO updateDTO) {
        boolean result = userServiceNew.updateUser(updateDTO);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户（管理员权限）
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @RequireAdmin
    public BaseResponse<Boolean> deleteUser(@RequestParam("id") Long id) {
        boolean result = userServiceNew.deleteUser(id);
        return ResultUtils.success(result);
    }
}
