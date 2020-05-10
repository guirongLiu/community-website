package com.example.springblog.service;

import com.example.springblog.Model.User;
import com.example.springblog.Model.UserExample;
import com.example.springblog.dto.GithubUser;
import com.example.springblog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(GithubUser githubUser, String token) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(String.valueOf(githubUser.getId()));
        List<User> users =userMapper.selectByExample(userExample);
        //System.out.println(users.size());
        if (users.size() == 0){
            User user = new User();
            user.setName(githubUser.getName());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setToken(token);
            userMapper.insertSelective(user);
        }
        else{
            User user = new User();
            user.setName(githubUser.getName());
            user.setAvatarUrl(githubUser.getAvatarUrl());
            user.setGmtModified(System.currentTimeMillis());
            user.setToken(token);
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(users.get(0).getId());
            userMapper.updateByExampleSelective(user,example);

        }
    }
}
