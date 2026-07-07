package com.zhimo.zhiyunpic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhimo.zhiyunpic.model.dto.picture.PictureQueryDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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


    /**
     * 将查询请求类转为QueryWrapper对象
     * @param pictureQueryDTO
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO pictureQueryDTO);

    /**
     * 获取图片响应包装类
     * @param picture 图片实体类
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片响应包装类（分页）
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 数据校验，用于更新和修改
     * @param picture
     */
    void validPicture(Picture picture);
}
