package com.example.springblog.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;

    private Long parentId;
    private Integer type;
    private String parentTypeName;
    private String parentTitle;

    private Long Notifier;
    private String NotifierName;

    private Long NotifiedUserId;

    private Integer status;
    private Long gmtCreate;
}
