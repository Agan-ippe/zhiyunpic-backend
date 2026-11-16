package com.zhimo.zhiyunpic.model.dto.space;

import com.zhimo.zhiyunpic.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-19 19:13
 * @Description 空间查询请求封装类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SpaceQueryDTO extends PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;
}
