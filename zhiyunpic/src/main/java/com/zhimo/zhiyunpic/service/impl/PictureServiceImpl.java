package com.zhimo.zhiyunpic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.manager.FileManager;
import com.zhimo.zhiyunpic.mapper.PictureMapper;
import com.zhimo.zhiyunpic.model.dto.file.UploadPictureDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureQueryDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureReviewDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.enums.PictureReviewStatusEnum;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;
import com.zhimo.zhiyunpic.model.vo.user.UserVO;
import com.zhimo.zhiyunpic.service.PictureService;
import com.zhimo.zhiyunpic.service.UserService;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import com.zhimo.zhiyunpic.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 93988
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2026-05-31 12:45:48
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService {

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;


    // regin 增删改查
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadDTO pictureUploadDTO, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 判断是新增还是更新
        Long pictureId = null;
        if (pictureUploadDTO != null) {
            pictureId = pictureUploadDTO.getId();
        }
        // 更新则需要判断图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或管理员可编辑图片
            if (oldPicture.getUserId().equals(loginUser.getId()) && UserUtils.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 上传图片
        // 将所有的图片都放在public目录下，并将用户上传的图片归类至对应的用户ID中
        String uploadPathPrefix = String.format("public/%s/", loginUser.getId());
        UploadPictureDTO uploadPictureDTO = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 构造入库图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureDTO.getUrl());
        picture.setName(uploadPictureDTO.getPicName());
        picture.setPicSize(uploadPictureDTO.getPicSize());
        picture.setPicWidth(uploadPictureDTO.getPicWidth());
        picture.setPicHeight(uploadPictureDTO.getPicHeight());
        picture.setPicScale(uploadPictureDTO.getPicScale());
        picture.setPicFormat(uploadPictureDTO.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 补充审核参数
        this.fillReviewParams(picture,loginUser);
        // 如果 pictureId 不为空则更新，反之则新增
        if (pictureId != null) {
            // 更新需要补充id 和 编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 操作数据库
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.DATABASE_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }


    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }
    // endregion

    // region 审核
    @Override
    public void doPictureReview(PictureReviewDTO pictureReviewDTO, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(pictureReviewDTO == null, ErrorCode.PARAMS_ERROR);
        Long id = pictureReviewDTO.getId();
        Integer reviewStatus = pictureReviewDTO.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验审核状态是否重复
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 数据库操作
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewDTO, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.DATABASE_ERROR);
    }
    // endregion

    // region 通用方法
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO pictureQueryDTO) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryDTO == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryDTO.getId();
        String name = pictureQueryDTO.getName();
        String introduction = pictureQueryDTO.getIntroduction();
        String category = pictureQueryDTO.getCategory();
        List<String> tags = pictureQueryDTO.getTags();
        Long picSize = pictureQueryDTO.getPicSize();
        Integer picWidth = pictureQueryDTO.getPicWidth();
        Integer picHeight = pictureQueryDTO.getPicHeight();
        Double picScale = pictureQueryDTO.getPicScale();
        String picFormat = pictureQueryDTO.getPicFormat();
        String searchText = pictureQueryDTO.getSearchText();
        Long userId = pictureQueryDTO.getUserId();
        String sortField = pictureQueryDTO.getSortField();
        String sortOrder = pictureQueryDTO.getSortOrder();
        Integer reviewStatus = pictureQueryDTO.getReviewStatus();
        String reviewMessage = pictureQueryDTO.getReviewMessage();
        Long reviewerId = pictureQueryDTO.getReviewerId();
        // 从多字段中搜索
        // searchText 支持同时从 name 和introduction 中检索，使用 qw 中的or语法构造查询条件
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            /* and (name like %xxx% or introduction like %xxx%) */
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // JSON 数组查询
        // 由于 tags 在数据库中是JSON格式的字符串，如果前端需要传多个 tag(必须同时存在才能查到)
        // 需要遍历 tags 数组，每个标签都使用 like 模糊查询，将这些条件组合在一起
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser){
        if (UserUtils.isAdmin(loginUser)) {
            // 管理员直接通过
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewTime(new Date());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动过审");
        }else {
            // 非管理员用户无论是编辑还是创建都是 待审核 状态
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    // endregion

}




