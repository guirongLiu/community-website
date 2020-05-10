package com.example.springblog.service;

import com.example.springblog.Model.Question;
import com.example.springblog.Model.QuestionExample;
import com.example.springblog.Model.User;
import com.example.springblog.Model.UserExample;
import com.example.springblog.dto.PaginationDTO;
import com.example.springblog.dto.QuestionDTO;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import com.example.springblog.mapper.QuestionExtMapper;
import com.example.springblog.mapper.QuestionMapper;
import com.example.springblog.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO<QuestionDTO> getList(String search, Integer page, Integer size) {
        if(!StringUtils.isBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }
        Question question = new Question();
        question.setTitle(search);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q->{
            QuestionDTO newQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, newQuestionDTO);
            return newQuestionDTO;
        }).collect(Collectors.toList());


        QuestionExample questionExample = new QuestionExample();
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        Integer totalPage = (totalCount % size == 0? totalCount/size:totalCount/size+1);

        if (page<1)
            page=1;
        if(page>totalPage)
            page=totalPage;

        Integer offset = size*(page-1);
        QuestionExample newQuestionExample = new QuestionExample();
        newQuestionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds( newQuestionExample, new RowBounds(offset,size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        for(Question question : questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setTargets(questionDTOS);
        paginationDTO.setPagination(totalPage,page);
        return paginationDTO;
    }

    public PaginationDTO<QuestionDTO> getList(Long userId, Integer page, Integer size) {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
        Integer totalPage = (totalCount % size == 0? totalCount/size:totalCount/size+1);

        if (totalPage==0)
            return paginationDTO;
        if (page<1)
            page=1;
        if(page>totalPage)
            page=totalPage;

        Integer offset = size*(page-1);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset,size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();


        for(Question question : questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setTargets(questionDTOS);
        paginationDTO.setPagination(totalPage,page);
        return paginationDTO;
    }

    public QuestionDTO getById(Long questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;

    }

    public void createOrUpdate(Question question, Long id) {
        if(id == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        }
        else{
            question.setGmtModified(System.currentTimeMillis());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(id);
            int updated = questionMapper.updateByExampleSelective(question, example);
            if(updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long questionId) {
        Question question = new Question();
        question.setId(questionId);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> getRelatedById(QuestionDTO questionDTO) {
        if(StringUtils.isBlank(questionDTO.getTag())){
            return new ArrayList<>();
        }
        String tagRegexp = StringUtils.replace(questionDTO.getTag(), ", ","|");

        tagRegexp = StringUtils.replace(tagRegexp, ",","|");

        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setTag(tagRegexp);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q->{
            QuestionDTO newQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, newQuestionDTO);
            return newQuestionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;
    }
}
