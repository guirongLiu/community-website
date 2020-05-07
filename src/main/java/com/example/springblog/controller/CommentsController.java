package com.example.springblog.controller;

import com.example.springblog.Model.Comment;
import com.example.springblog.Model.User;
import com.example.springblog.dto.CommentCreateDTO;
import com.example.springblog.dto.CommentDTO;
import com.example.springblog.dto.ResultDTO;
import com.example.springblog.enums.CommentTypeEnum;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentsController {
    @Autowired
    private CommentService commentServie;

    @ResponseBody
    @RequestMapping(value="/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentDTO,
                       HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.USER_NOT_LOGIN);
        }
        String content = commentDTO.getContent();
        if ( commentDTO == null || StringUtils.isBlank(content)){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(content);
        comment.setType(commentDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified((comment.getGmtCreate()));
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentServie.insert(comment);
        return ResultDTO.okOf();

    }

    @ResponseBody
    @RequestMapping(value="/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> secondComments(@PathVariable(name = "id") Long id){
        List<CommentDTO> secCommentDTOs = commentServie.getCommentsByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(secCommentDTOs);
    }
}
