package com.example.springblog.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String category;
    private List<String> categoryTags;
}
