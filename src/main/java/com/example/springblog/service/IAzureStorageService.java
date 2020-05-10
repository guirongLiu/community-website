package com.example.springblog.service;

import com.example.springblog.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAzureStorageService {

    /**
     * 上传文件(图片)
     * @param type 文件类型
     * @param multipartFiles 文件
     * @return
     */
    List<FileDTO> uploadFile(String type, MultipartFile[] multipartFiles);
}
