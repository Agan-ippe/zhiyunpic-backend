package com.zhimo.zhiyunpic.common;

import lombok.Data;

/**
 * @Author <a href="https://github.com/Agan-ippe">知莫</a>
 * @Date 2026/02/23   13:48
 * @Version 1.0
 * @Description 分页请求参数
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

