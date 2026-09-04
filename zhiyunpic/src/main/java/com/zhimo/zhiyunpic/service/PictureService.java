package com.zhimo.zhiyunpic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhimo.zhiyunpic.model.dto.picture.PictureQueryDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureReviewDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadByBatchDTO;
import com.zhimo.zhiyunpic.model.dto.picture.PictureUploadDTO;
import com.zhimo.zhiyunpic.model.entity.Picture;
import com.zhimo.zhiyunpic.model.entity.User;
import com.zhimo.zhiyunpic.model.vo.picture.PictureVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 93988
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-05-31 12:45:48
*/
public interface PictureService extends IService<Picture> {

    // region 增删改查
    /**
     * 上传图片
     *
     * @param inputSource    上传的文件
     * @param pictureUploadDTO 图片上传请求包装类
     * @param loginUser        当前登录用户
     * @return PictureVO 图片响应包装类
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadDTO pictureUploadDTO,
                            User loginUser);

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

    // endregion
    // region 审核
    /**
     * 图片审核
     * @param pictureReviewDTO 图片审核请求包装类
     * @param loginUser        当前登录用户
     */
    void doPictureReview(PictureReviewDTO pictureReviewDTO, User loginUser);
    // endregion
    // region 通用方法
    /**
     * 将查询请求类转为QueryWrapper对象
     * @param pictureQueryDTO
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO pictureQueryDTO);

    /**
     * 数据校验，用于更新和修改
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 填充审核参数
     * @param picture 图片实体类
     * @param loginUser 当前登录用户
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     * @param pictureUploadByBatchDTO
     * @param loginUser
     * @return 成功创建的图片数
     */
    int uploadPictureByBatch(
            PictureUploadByBatchDTO pictureUploadByBatchDTO,
            User loginUser
    );

    // endregion

    /**
     * 图片列表的多级缓存
     * @return
     */
    Page<PictureVO> getPictureVOPageWithCache(PictureQueryDTO pictureQueryDTO, HttpServletRequest request);

}
