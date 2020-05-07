package com.example.springblog.controller;


import com.example.springblog.Model.User;
import com.example.springblog.dto.NotificationDTO;
import com.example.springblog.dto.PaginationDTO;
import com.example.springblog.dto.QuestionDTO;
import com.example.springblog.mapper.UserMapper;
import com.example.springblog.service.NotificationService;
import com.example.springblog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name="action") String action,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "4") Integer size,
                          HttpServletRequest request,
                          Model model){
        User user= (User) request.getSession().getAttribute("user");

        if (user == null){
            model.addAttribute("loginError","The user hasn't logged in.");
            return "profile";
        }

        if(action.contains("myQuestion")){
            model.addAttribute("section","myQuestions");
            model.addAttribute("sectionName","myQuestions");
            PaginationDTO<QuestionDTO> paginationDTO = questionService.getList(user.getId(),page,size);
            model.addAttribute("pagination", paginationDTO);
        }else if(action.contains("notification")){
            model.addAttribute("section","notifications");
            model.addAttribute("sectionName","notifications");
            PaginationDTO<NotificationDTO> paginationDTO = notificationService.getList(user.getId(),page,size);
            model.addAttribute("pagination", paginationDTO);
        }
        return "profile";
    }
}
