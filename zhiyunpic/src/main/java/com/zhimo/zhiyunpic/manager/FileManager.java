package com.zhimo.zhiyunpic.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.zhimo.zhiyunpic.config.CosClientConfig;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.model.dto.file.UploadPictureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

import static com.zhimo.zhiyunpic.utils.FileUtils.deleteTempFile;
import static com.zhimo.zhiyunpic.utils.FileUtils.validPicture;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-06-07 11:20
 * @Description 文件上传通用
 */
@Slf4j
@Service
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param multipartFile    上传的文件
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureDTO uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验图片
        validPicture(multipartFile);
        // 图片上传地址
        String uuid = RandomUtil.randomString(8);
        String originalFilename = multipartFile.getOriginalFilename();
        // 不要使用原始文件名称，增加安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid,
                FileUtil.getSuffix(originalFilename)
        );
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
        // 解析结果返回
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadPath, null);
            // 将前端上传的 MultipartFile 数据转移到刚创建的本地临时文件中
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取图片宽高比
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            // 封装返回结果
            UploadPictureDTO uploadPictureDTO = new UploadPictureDTO();
            uploadPictureDTO.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureDTO.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureDTO.setPicSize(FileUtil.size(file));
            uploadPictureDTO.setPicWidth(picWidth);
            uploadPictureDTO.setPicHeight(picHeight);
            uploadPictureDTO.setPicScale(picScale);
            uploadPictureDTO.setPicFormat(imageInfo.getFormat());
            // 返回可访问地址
            return uploadPictureDTO;
        } catch (Exception e) {
            log.error("图片上传对象存储失败：" + e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 通过URL上传图片
     * @param fileUrl 文件地址
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureDTO uploadPictureByUrl(String fileUrl, String uploadPathPrefix) {
        // 校验图片
//        validPicture(multipartFile);
        validPicture(fileUrl);
        // 图片上传地址
        String uuid = RandomUtil.randomString(8);
//        String originalFilename = multipartFile.getOriginalFilename();
        String originalFileName = FileUtil.mainName(fileUrl);
        // 不要使用原始文件名称，增加安全性
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()),
                uuid,
                FileUtil.getSuffix(originalFileName)
        );
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
        // 解析结果返回
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(uploadPath, null);
            // 将前端上传的 MultipartFile 数据转移到刚创建的本地临时文件中
//            multipartFile.transferTo(file);
            // 下载文件
            HttpUtil.downloadFile(fileUrl, file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取图片宽高比
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            // 封装返回结果
            UploadPictureDTO uploadPictureDTO = new UploadPictureDTO();
            uploadPictureDTO.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
            uploadPictureDTO.setPicName(FileUtil.mainName(originalFileName));
            uploadPictureDTO.setPicSize(FileUtil.size(file));
            uploadPictureDTO.setPicWidth(picWidth);
            uploadPictureDTO.setPicHeight(picHeight);
            uploadPictureDTO.setPicScale(picScale);
            uploadPictureDTO.setPicFormat(imageInfo.getFormat());
            // 返回可访问地址
            return uploadPictureDTO;
        } catch (Exception e) {
            log.error("图片上传对象存储失败：" + e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 清理临时文件
            deleteTempFile(file);
        }
    }
}
