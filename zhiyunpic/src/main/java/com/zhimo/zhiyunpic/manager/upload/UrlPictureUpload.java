package com.zhimo.zhiyunpic.manager.upload;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.zhimo.zhiyunpic.exception.BusinessException;
import com.zhimo.zhiyunpic.exception.ErrorCode;
import com.zhimo.zhiyunpic.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.zhimo.zhiyunpic.constants.file.FileConstants.*;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-08-01 19:19
 * @Description Url上传
 */
@Slf4j
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
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
                    ThrowUtils.throwIf(contentLength > UPLOAD_FILE_MAX_SIZE, ErrorCode.PARAMS_ERROR, "文件大小不能超过" + UPLOAD_FILE_MAX_SIZE + "M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        }
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        // https://vcg05.cfp.cn/creative/vcg/nowater800/new/VCG211292977057.jpg?x-oss-process=image/format,webp
        // https://cdn.xiaolincoding.com//picgo/a50d417069e2b228c2f6cb158dcd52cb.jpeg
        // https://static.www.tencent.com/uploads/2026/07/06/10c8b5b34b4793c92e448b2656379b6e.png!article.cover
        // https://i0.hdslb.com/bfs/face/781f387583f8aba6b6c450a213f7cb33ac43cb0d.jpg@240w_240h_1c_1s_!web-avatar-nav.avif
        // 1. 去除 URL 查询参数 (如 ?x-oss-process=image/format,webp)
        String urlWithoutParams = fileUrl.split("\\?")[0];
        // 去除 URL 路径末尾的图片处理样式标识 (如 !article.cover)
        urlWithoutParams = urlWithoutParams.split("!")[0];
        urlWithoutParams = urlWithoutParams.split("@")[0];
        // 2. 获取路径的最后一部分 (如 VCG211292977057.jpg)
        String fileNameWithExt = urlWithoutParams.substring(urlWithoutParams.lastIndexOf("/") + 1);
        // 3. 截取最后一位小数点之前的字符串作为主文件名
        // https://tse1-mm.cn.bing.net/th/id/OIP-C.WJrTnBO6PFi5YnzqT8d-KwHaG7
        int lastDotIndex = fileNameWithExt.lastIndexOf(".");
        String suffix = fileNameWithExt.substring(lastDotIndex + 1);
        // 如果后缀包含合法图片后缀
        if (RAW_DATA_SUFFIX_LIST.contains(suffix.toLowerCase())){
            if (lastDotIndex > 0) {
                String mainName = fileNameWithExt.substring(0, lastDotIndex);
                // 4. 拼接后缀，返回完整文件名 (如 VCG211292977057.jpg)
                return mainName + suffix;
            }
        }
        // 如果没有后缀，直接返回
        return fileNameWithExt + ".jpeg";
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }
}
