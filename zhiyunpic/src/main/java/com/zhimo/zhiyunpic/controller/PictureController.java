package com.zhimo.zhiyunpic.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhimo.zhiyunpic.annotation.AuthCheck;
import com.zhimo.zhiyunpic.common.BaseResponse;
import com.zhimo.zhiyunpic.common.DeleteRequest;
import com.zhimo.zhiyunpic.constants.user.UserConstants;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.model.dto.picture.*;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.enums.PictureReviewStatusEnum;
import com.zhimo.zhiyunpic.model.vo.picture.PictureTagCategory;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;
import com.zhimo.zhiyunpic.service.PictureService;
import com.zhimo.zhiyunpic.service.UserService;
import com.zhimo.zhiyunpic.utils.ResultUtils;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import com.zhimo.zhiyunpic.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-06-30 11:14
 * @Description 图片相关接口
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;


    // region -- 以下为增删改查业务
    /**
     * 上传图片（可重新上传）
     * @param multipartFile 文件
     * @param pictureUploadDTO 上传参数
     * @param request 请求
     * @return
     */
    @PostMapping("/upload/file")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadDTO pictureUploadDTO,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadDTO, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 通过 url上传图片（可重新上传）
     * @param pictureUploadDTO
     * @param request
     * @return
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadDTO pictureUploadDTO,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = pictureUploadDTO.getFileUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadDTO, loginUser);
        return ResultUtils.success(pictureVO);
    }


    /**
     * 删除图片
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long id = deleteRequest.getId();
        // 判断是否存在
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 只有本人或管理员可删除
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !UserUtils.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.DATABASE_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新图片（仅管理员可用）
     * @param pictureUpdateDTO
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateDTO pictureUpdateDTO, HttpServletRequest request){
        if (pictureUpdateDTO == null || pictureUpdateDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateDTO, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateDTO.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        Long id = pictureUpdateDTO.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 补充审核参数
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture,loginUser);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.DATABASE_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 编辑图片（仅本人或管理员可用）
     * @param pictureEditDTO
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditDTO pictureEditDTO, HttpServletRequest request){
        if (pictureEditDTO == null || pictureEditDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditDTO, picture);
        // JSON 数组转为字符串
        picture.setTags(JSONUtil.toJsonStr(pictureEditDTO.getTags()));
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long id = pictureEditDTO.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 只有本人或管理员可编辑
        User loginUser = userService.getLoginUser(request);
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !UserUtils.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 补充审核参数
        pictureService.fillReviewParams(picture,loginUser);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.DATABASE_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据id查询图片（仅管理员）
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(Long id){
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(picture);
    }

    /**
     * 根据id查询图片包装类
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(Long id, HttpServletRequest request){
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取包装类
        return ResultUtils.success(pictureService.getPictureVO(picture,request));
    }

    /**
     * 分页查询图片（管理员）
     * @param pictureQueryDTO
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryDTO pictureQueryDTO){
        long current = pictureQueryDTO.getCurrent();
        long pageSize = pictureQueryDTO.getPageSize();
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize),
                pictureService.getQueryWrapper(pictureQueryDTO));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页查询图片封装类
     * @param pictureQueryDTO
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryDTO pictureQueryDTO,HttpServletRequest request){
        long current = pictureQueryDTO.getCurrent();
        long pageSize = pictureQueryDTO.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20 , ErrorCode.OPERATION_ERROR);
        // 普通用户默认只能查看已过审的数据
        pictureQueryDTO.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize),
                pictureService.getQueryWrapper(pictureQueryDTO));
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage,request));
    }
    // endregion

    // region 图片审核
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstants.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewDTO pictureReviewDTO, HttpServletRequest request){
        ThrowUtils.throwIf(pictureReviewDTO == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewDTO, loginUser);
        return ResultUtils.success(true);
    }

    // endregion

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }


}
