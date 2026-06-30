package com.zhimo.zhiyunpic.service;

import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 93988
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-05-31 12:45:48
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile    上传的文件
     * @param pictureUploadDTO 图片上传请求包装类
     * @param loginUser        当前登录用户
     * @return PictureVO 图片响应包装类
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadDTO pictureUploadDTO,
                            User loginUser);

}
