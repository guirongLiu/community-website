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

        commentMapper.insertSelective(comment);


        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            Comment dbComment = commentMapper.selectByPrimaryKey((comment.getParentId()));
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            dbComment.setCommentCount(1);
            commentExtMapper.incCommentCount(dbComment);

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            User commentator = userMapper.selectByPrimaryKey(comment.getCommentator());
            if (comment.getCommentator() != dbComment.getCommentator()){

                createNotification(comment,question.getId(),question.getTitle(),dbComment.getCommentator(),commentator.getName());
            }
            if(comment.getCommentator() != question.getCreator()){
                createNotification(comment,question.getId(),question.getTitle(),question.getCreator(), commentator.getName());
            }


        }else{
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);


            User commentator = userMapper.selectByPrimaryKey(comment.getCommentator());
            if(comment.getCommentator() != question.getCreator())
                createNotification(comment,question.getId(),question.getTitle(),question.getCreator(), commentator.getName());
        }


    }

    public void createNotification(Comment comment, Long questionId, String questionTitle, Long notifiedUserId, String notifierName){
        Notification notification = new Notification();
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setNotifier(comment.getCommentator());
        notification.setType(comment.getType());
        notification.setGmtCreate(comment.getGmtCreate());
        notification.setNotifiedUserId(notifiedUserId);
        notification.setNotifierName(notifierName);
        notification.setParentId(questionId);
        notification.setParentTitle(questionTitle);
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
