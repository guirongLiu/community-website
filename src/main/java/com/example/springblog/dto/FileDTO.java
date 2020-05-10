package com.example.springblog.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileDTO {
    private int success;
    private String message;
    private String fileName;
    private String url;
    private String thumbnailUrl;
}
