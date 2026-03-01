package com.zhimo.zhiyunpic.controller;

import com.zhimo.zhiyunpic.annotation.AuthCheck;
import com.zhimo.zhiyunpic.common.BaseResponse;
import com.zhimo.zhiyunpic.common.DeleteRequest;
import com.zhimo.zhiyunpic.constants.user.UserConstants;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.model.dto.user.*;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.user.UserLoginVO;
import com.zhimo.zhiyunpic.model.vo.user.UserVO;
import com.zhimo.zhiyunpic.service.UserService;
import com.zhimo.zhiyunpic.utils.ResultUtils;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import com.zhimo.zhiyunpic.utils.UserUtils;
import org.springframework.beans.BeanUtils;
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
    private UserService userService;

    //    region 登录相关
    /**
     * 用户注册
     *
     * @param userRegisterDTO 注册信息
     * @return 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        ThrowUtils.throwIf(userRegisterDTO == null,ErrorCode.PARAMS_ERROR);
        long userId = userService.userRegister(
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
        UserLoginVO userLoginVO = userService.userLogin(
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

    //    endregion
    //    region 增删改查


    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDTO userAddDTO) {
        ThrowUtils.throwIf(userAddDTO == null, ErrorCode.PARAMS_ERROR);
        if (userAddDTO.getUserRole() == null) {
            userAddDTO.setUserRole(UserConstants.DEFAULT_ROLE);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddDTO, user);
        String encryptPassword = UserUtils.getEncryptPassword(UserConstants.DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据ID获取用户信息（仅管理员）
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取脱敏用户信息
     * @param id 用户id
     * @return UserVO
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id){
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(UserUtils.getUserVO(user));
    }

    /**
     * 查询用户列表（仅管理员）
     *
     * @param queryDTO 查询条件
     * @return 用户列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<List<UserVO>> listUsers(@RequestBody UserQueryDTO queryDTO) {
        ThrowUtils.throwIf(queryDTO == null, ErrorCode.PARAMS_ERROR);
        List<UserVO> UserVOList = userService.listUsers(queryDTO);
        return ResultUtils.success(UserVOList);
    }

    /**
     * 更新用户信息（管理员权限）
     *
     * @param updateDTO 更新信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO updateDTO) {
        if (updateDTO == null || updateDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.updateUser(updateDTO);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户（管理员权限）
     *
     * @param request 用户ID
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest request) {
        if (request == null || request.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.removeById(request.getId()));
    }
}
