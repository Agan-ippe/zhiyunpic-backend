package com.zhimo.zhiyunpic.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-19 19:14
 * @Description 空间更新请求封装类，仅管理员可用，修改空间级别和限额
 */
@Data
public class SpaceUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;
}
