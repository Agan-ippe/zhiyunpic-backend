package com.zhimo.zhiyunpic.model.dto.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @date 2026-05-31 12:55
 * @description 图片上传 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureUploadDTO implements Serializable {

    private static final long serialVersionUID = 4044668964827028747L;
    /**
     * 图片 id
     */
    private Long id;

}
