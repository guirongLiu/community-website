package com.example.springblog.controller;

import com.example.springblog.dto.CommentDTO;
import com.example.springblog.dto.QuestionDTO;
import com.example.springblog.enums.CommentTypeEnum;
import com.example.springblog.service.CommentService;
import com.example.springblog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String questionDetail(@PathVariable(name="id") Long questionId,
                                 Model model){
        QuestionDTO questionDTO = questionService.getById(questionId);
        questionService.incView(questionId);
        model.addAttribute("questionDTO", questionDTO);
        List<CommentDTO> commentDTOs = commentService.getCommentsByTargetId(questionId, CommentTypeEnum.QUESTION);
        model.addAttribute("commentDTOs",commentDTOs);
        List<QuestionDTO> relatedQuestions = questionService.getRelatedById(questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
