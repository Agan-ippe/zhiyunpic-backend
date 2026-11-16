package com.zhimo.zhiyunpic.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-19 19:14
 * @Description 空间编辑请求封装类，面向用户，目前只支持修改空间名称
 */
@Data
public class SpaceEditDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
}
