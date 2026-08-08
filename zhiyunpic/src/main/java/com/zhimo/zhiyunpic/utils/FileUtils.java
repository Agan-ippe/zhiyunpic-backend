package com.zhimo.zhiyunpic.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

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

}
