package com.example.springblog.controller;

import com.example.springblog.Model.User;
import com.example.springblog.dto.NotificationDTO;
import com.example.springblog.enums.NotificationTypeEnum;
import com.example.springblog.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id){
        User user = (User) request.getSession().getAttribute("user");

        if(user == null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id,user);

        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType())
            return "redirect:/question/"+ notificationDTO.getParentId();
        else{
            return "redirect:/";
        }

    }
}
