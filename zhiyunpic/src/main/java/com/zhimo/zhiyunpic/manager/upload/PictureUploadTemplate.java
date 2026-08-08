package com.zhimo.zhiyunpic.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.zhimo.zhiyunpic.config.CosClientConfig;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.manager.CosManager;
import com.zhimo.zhiyunpic.model.dto.file.UploadPictureDTO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

import static com.zhimo.zhiyunpic.utils.FileUtils.deleteTempFile;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-08-01 17:31
 * @Description 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;
    /**
     * 上传图片
     *
     * @param inputSource    上传的文件
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    // 传参改成 Object类方便接收 file 和 url 两种类型，如果入参类型较多可以考虑用泛型
    public UploadPictureDTO uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 图片上传地址
        String uuid = RandomUtil.randomString(8);
        String originFilename = getOriginalFilename(inputSource);
        // 不要使用原始文件名称，增加安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        // 解析结果返回
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 将前端上传的 MultipartFile 数据转移到刚创建的本地临时文件中
            processFile(inputSource, file);
            // 上传到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 返回封装结果
            return buildResult(originFilename, file, uploadPath, imageInfo);
        } catch (Exception e) {
            log.error("图片上传对象存储失败：" , e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 校验文件（本地或url）
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取原始文件名
     * @param inputSource
     * @return
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     * @param inputSource
     * @param file
     * @throws Exception
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    private UploadPictureDTO buildResult(String originalFilename, File file, String uploadPath, ImageInfo imageInfo) {
        UploadPictureDTO uploadPictureDTO = new UploadPictureDTO();
        // 获取图片宽高比
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        // 封装结果
        uploadPictureDTO.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureDTO.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureDTO.setPicSize(FileUtil.size(file));
        uploadPictureDTO.setPicWidth(picWidth);
        uploadPictureDTO.setPicHeight(picHeight);
        uploadPictureDTO.setPicScale(picScale);
        uploadPictureDTO.setPicFormat(imageInfo.getFormat());
        return uploadPictureDTO;
    }
}
