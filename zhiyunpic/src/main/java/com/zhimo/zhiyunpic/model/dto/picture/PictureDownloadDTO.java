package com.zhimo.zhiyunpic.model.dto.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Agan-ippe">知莫</a>
 * @version 1.0
 * @Date 2026-09-10 16:27
 * @Description 图片下载DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureDownloadDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 下载次数
     */
    private Long downloadCount;
}
