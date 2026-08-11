package com.zhimo.zhiyunpic.utils;

import cn.hutool.core.util.StrUtil;
import com.zhimo.zhiyunpic.model.dto.file.UploadPictureDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.zhimo.zhiyunpic.model.entity.User;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-08-11 16:22
 * @Description 图片工具类
 */
@Slf4j
public class PictureUtils {
    private PictureUtils(){}

    public static Picture buildPicture(UploadPictureDTO uploadPictureDTO, PictureUploadDTO pictureUploadDTO, User loginUser){
        // 构造入库图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureDTO.getUrl());
        String picName = uploadPictureDTO.getPicName();
        if (pictureUploadDTO != null && StrUtil.isNotBlank(pictureUploadDTO.getPicName())) {
            picName = pictureUploadDTO.getPicName();
        }
        picture.setName(picName);
        picture.setPicSize(uploadPictureDTO.getPicSize());
        picture.setPicWidth(uploadPictureDTO.getPicWidth());
        picture.setPicHeight(uploadPictureDTO.getPicHeight());
        picture.setPicScale(uploadPictureDTO.getPicScale());
        picture.setPicFormat(uploadPictureDTO.getPicFormat());
        picture.setUserId(loginUser.getId());
        return picture;
    }
}
