package com.example.springblog.controller;

import com.example.springblog.Model.User;
import com.example.springblog.dto.AccessTokenDTO;
import com.example.springblog.mapper.UserMapper;
import com.example.springblog.provider.GithubProvider;
import com.example.springblog.dto.GithubUser;
import com.example.springblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;
    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response)throws Exception{
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setClient_id((clientId));
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        //return "redirect:/";
        if(githubUser != null) {
            String token = UUID.randomUUID().toString();
            userService.createOrUpdate(githubUser,token);
            //login successfully
            response.addCookie(new Cookie("token",token));
            request.getSession().setAttribute("user",githubUser);
        }else{
            log.error("callback get github error,{}",githubUser);
            //login failure
        }
        return "redirect:/";
        //System.out.println(user.getId()+" "+user.getName()+" "+user.getBio());
        //return "Index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";

    }

}
