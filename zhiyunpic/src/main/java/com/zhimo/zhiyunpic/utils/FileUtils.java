package com.zhimo.zhiyunpic.utils;

import cn.hutool.core.io.FileUtil;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.zhimo.zhiyunpic.constants.file.FileConstants.ONE_MB;
import static com.zhimo.zhiyunpic.constants.file.FileConstants.RAW_DATA_SUFFIX_LIST;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-06-26 10:53
 * @Description 文件工具类
 */

@Slf4j
public class FileUtils {

    /**
     * 删除临时文件
     *
     * @param file 文件
     */
    public static void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

    /**
     * 校验文件
     *
     * @param file 文件
     */
    public static void validPicture(MultipartFile file) {
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 校验文件大小
        long fileSize = file.getSize();
        ThrowUtils.throwIf(fileSize > ONE_MB * 10, ErrorCode.PARAMS_ERROR, "文件大小不能超过10MB");
        // 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        ThrowUtils.throwIf(!RAW_DATA_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型不支持");


    }
}
