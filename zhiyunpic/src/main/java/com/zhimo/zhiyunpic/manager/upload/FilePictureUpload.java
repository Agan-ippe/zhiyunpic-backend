package com.zhimo.zhiyunpic.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.zhimo.zhiyunpic.constants.file.FileConstants.RAW_DATA_SUFFIX_LIST;
import static com.zhimo.zhiyunpic.constants.file.FileConstants.UPLOAD_FILE_MAX_SIZE;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-08-01 18:26
 * @Description 本地文件上传
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile file = (MultipartFile) inputSource;
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 校验文件大小
        long fileSize = file.getSize();
        ThrowUtils.throwIf(fileSize > UPLOAD_FILE_MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过" + UPLOAD_FILE_MAX_SIZE + "MB");
        // 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        ThrowUtils.throwIf(!RAW_DATA_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型不支持");
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile file = (MultipartFile) inputSource;
        return file.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);

    }
}
