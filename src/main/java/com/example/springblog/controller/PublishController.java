package com.example.springblog.controller;

import com.example.springblog.Model.Question;
import com.example.springblog.Model.User;
import com.example.springblog.cache.CacheTags;
import com.example.springblog.dto.QuestionDTO;

import com.example.springblog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;


    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name="id") Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);

        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tagDTOs", CacheTags.getTagDTOS());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model){
        model.addAttribute("tagDTOs", CacheTags.getTagDTOS());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "tag") String tag,
            @RequestParam(name = "id",required = false) Long id,
            HttpServletRequest request,
            Model model){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tagDTOs", CacheTags.getTagDTOS());
        //front end and back end both need verify
        if(title == null || title == ""){
            model.addAttribute("error","The title cannot be empty.");
            return "publish";
        }
        if(description == null || description == ""){
            model.addAttribute("error","The description cannot be empty.");
            return "publish";
        }
        if(tag == null || tag == ""){
            model.addAttribute("error","The tag cannot be empty.");
            return "publish";
        }
        String invalid = CacheTags.filterInvalid(tag);
        if(invalid.length()>0){
            model.addAttribute("error","Invalid tags:" + invalid);
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");

        if (user == null){
            model.addAttribute("error","The user hasn't logged in.");
            return "publish";
        }

        //System.out.println(title);
        Question question = new Question();
        question.setTitle(title);
        question.setDescription((description));
        question.setTag(tag);
        question.setCreator(user.getId());

        questionService.createOrUpdate(question,id);
        return "redirect:/";
    }
}
