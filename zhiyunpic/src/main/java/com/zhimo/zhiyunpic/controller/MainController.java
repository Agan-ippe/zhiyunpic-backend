package com.zhimo.zhiyunpic.controller;

import com.zhimo.zhiyunpic.common.BaseResponse;
import com.zhimo.zhiyunpic.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author <a href="https://github.com/Agan-ippe">知莫</a>
 * @Date 2026/02/23   14:06
 * @Version 1.0
 * @Description 代码健康检查，用于检查公共组件和封装的返回值是否正常
 */
@RestController
@RequestMapping("/")
public class MainController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}

