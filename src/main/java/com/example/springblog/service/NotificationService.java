package com.example.springblog.service;

import com.example.springblog.Model.*;
import com.example.springblog.dto.NotificationDTO;
import com.example.springblog.dto.PaginationDTO;
import com.example.springblog.dto.QuestionDTO;
import com.example.springblog.enums.NotificationStatusEnum;
import com.example.springblog.enums.NotificationTypeEnum;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import com.example.springblog.mapper.NotificationMapper;
import com.example.springblog.mapper.UserMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.NotificationEmitter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    public NotificationDTO read(Long notificationId, User user){
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);

        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getNotifiedUserId(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }


        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setParentTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));

        return notificationDTO;

    }

    public PaginationDTO<NotificationDTO> getList(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andNotifiedUserIdEqualTo(userId);

        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        Integer totalPage = (totalCount % size == 0? totalCount/size:totalCount/size+1);

        if (totalPage==0)
            return paginationDTO;
        if (page<1)
            page=1;
        if(page>totalPage)
            page=totalPage;

        Integer offset = size*(page-1);
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andNotifiedUserIdEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset,size));
        List<NotificationDTO> notificationDTOs = new ArrayList<>();


        for(Notification notification : notifications){
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setParentTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));
            notificationDTOs.add(notificationDTO);
        }
        paginationDTO.setTargets(notificationDTOs);
        paginationDTO.setPagination(totalPage,page);
        return paginationDTO;
    }

    public Long getUnreadCount(Long userId) {
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andNotifiedUserIdEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return  notificationMapper.countByExample(example);
    }
}
