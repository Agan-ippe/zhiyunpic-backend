package com.zhimo.zhiyunpic.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-18 18:38
 * @Description 空间创建请求封装类
 */
@Data
public class SpaceAddDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

}
