package com.zhimo.zhiyunpic.constants.file;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-06-26 10:34
 * @Description 文件常量
 */
public interface FileConstants {
    /**
     * 文件基础大小
     */
    long ONE_MB = 1024 * 1024;

    /**
     * 原始数据文件后缀
     */
    List<String> RAW_DATA_SUFFIX_LIST = Arrays.asList("jpeg", "png", "jpg", "webp", "gif");
}
