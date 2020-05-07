package com.example.springblog.controller;

import com.example.springblog.Model.Question;
import com.example.springblog.Model.User;
import com.example.springblog.dto.PaginationDTO;
import com.example.springblog.dto.QuestionDTO;
import com.example.springblog.mapper.QuestionMapper;
import com.example.springblog.mapper.UserMapper;
import com.example.springblog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "4") Integer size,
                        Model model){

        PaginationDTO pagination = questionService.getList(page,size);

        model.addAttribute("pagination",pagination);
        return "index";
    }
}
