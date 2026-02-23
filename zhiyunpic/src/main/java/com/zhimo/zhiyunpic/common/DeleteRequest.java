package com.zhimo.zhiyunpic.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author <a href="https://github.com/Agan-ippe">知莫</a>
 * @Date 2026/02/23   13:47
 * @Version 1.0
 * @Description 删除请求包装类
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

