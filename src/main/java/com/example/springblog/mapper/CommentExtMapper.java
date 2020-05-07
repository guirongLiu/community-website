package com.example.springblog.mapper;

import com.example.springblog.Model.Comment;
import com.example.springblog.Model.CommentExample;
import com.example.springblog.Model.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment record);
}
