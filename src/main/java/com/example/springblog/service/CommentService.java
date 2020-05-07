package com.example.springblog.service;

import com.example.springblog.Model.*;
import com.example.springblog.dto.CommentDTO;
import com.example.springblog.enums.CommentTypeEnum;
import com.example.springblog.enums.NotificationStatusEnum;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import com.example.springblog.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    //@Autowired
    //private  NotificationService notificationService;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARENT_NOT_FOUND);
        }
        if (!CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);

        }

        Notification notification = new Notification();
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setNotifier(comment.getCommentator());
        notification.setType(comment.getType());
        notification.setGmtCreate(comment.getGmtCreate());

        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            Comment dbComment = commentMapper.selectByPrimaryKey((comment.getParentId()));
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            //comment.setId(0L);
            commentMapper.insertSelective(comment);
            dbComment.setCommentCount(1);
            commentExtMapper.incCommentCount(dbComment);

            notification.setParentId(dbComment.getId());
            notification.setNotifiedUserId(dbComment.getCommentator());
            User parentUser = userMapper.selectByPrimaryKey(dbComment.getCommentator());
            notification.setNotifierName(parentUser.getName());
            notification.setParentTitle(dbComment.getContent());
        }else{
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //comment.setId(0L);
            commentMapper.insertSelective(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            notification.setParentId(question.getId());
            notification.setNotifiedUserId(question.getCreator());
            User parentUser = userMapper.selectByPrimaryKey(question.getCreator());
            notification.setNotifierName(parentUser.getName());
            notification.setParentTitle(question.getTitle());
        }
        notificationMapper.insert(notification);

    }

    public List<CommentDTO> getCommentsByTargetId(Long questionId,CommentTypeEnum type) {
        //get list of comments
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andTypeEqualTo(type.getType())
                .andParentIdEqualTo(questionId);
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);

        if (comments.size()==0){
            return new ArrayList<CommentDTO>();
        }
        //get list of users
        Set<Long> userIds = comments.stream().map(comment->comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIdList = new ArrayList<>();
        userIdList.addAll(userIds);

        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIdList);
        List<User> users = userMapper.selectByExample(userExample);
        //map between id and user
        Map<Long,User> userMap= users.stream().collect(Collectors.toMap(user->user.getId(),user->user));

        //map between comment and user
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOs;
    }
}
