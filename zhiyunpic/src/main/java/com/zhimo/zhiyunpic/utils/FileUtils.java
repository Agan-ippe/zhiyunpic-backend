package com.zhimo.zhiyunpic.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.zhimo.zhiyunpic.constants.file.FileConstants.*;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-06-26 10:53
 * @Description 文件工具类
 */

@Slf4j
public class FileUtils {
    private FileUtils() {
        /* This utility class should not be instantiated */
    }


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
        ThrowUtils.throwIf(fileSize > UPLOAD_FILE_MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过" + UPLOAD_FILE_MAX_SIZE + "MB");
        // 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(file.getOriginalFilename());
        ThrowUtils.throwIf(!RAW_DATA_SUFFIX_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型不支持");
    }

    /**
     * 校验文件 url方式上传
     * @param fileUrl
     */
    public static void validPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");

        try {
            // 1. 验证 URL 格式，验证是否是合法的 URL
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");
        // 3. 发送 HEAD 请求以验证文件是否存在
        // 使用 try with resources 语法简化资源关闭
        try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > UPLOAD_FILE_MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过"+ UPLOAD_FILE_MAX_SIZE +"M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        }
    }

}
