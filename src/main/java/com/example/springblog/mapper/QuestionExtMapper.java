package com.example.springblog.mapper;

import com.example.springblog.Model.Question;
import com.example.springblog.Model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question record);
    List<Question> selectSearch(Question record);

}