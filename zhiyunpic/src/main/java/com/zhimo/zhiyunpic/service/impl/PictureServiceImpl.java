package com.zhimo.zhiyunpic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.manager.FileManager;
import com.zhimo.zhiyunpic.mapper.PictureMapper;
import com.zhimo.zhiyunpic.model.dto.file.UploadPictureDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;
import com.zhimo.zhiyunpic.service.PictureService;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

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
//            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
//            queryWrapper.eq("id", pictureId);
//            boolean exists1 = this.exists(queryWrapper);
            // 以上为lambda表达式的对照
            boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
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
}




