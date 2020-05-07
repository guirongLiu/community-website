package com.example.springblog.cache;

import com.example.springblog.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CacheTags {

    public static List<TagDTO> getTagDTOS(){
        List<TagDTO> tagDTOS = new ArrayList<>();

        TagDTO program = new TagDTO();
        program.setCategory("Program");
        program.setCategoryTags(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "asp.net", "lua", "scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));
        tagDTOS.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategory("Platform");
        framework.setCategoryTags(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails", "tornado", "koa", "struts"));
        tagDTOS.add(framework);


        TagDTO server = new TagDTO();
        server.setCategory("Server");
        server.setCategoryTags(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存 tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategory("Database");
        db.setCategoryTags(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql memcached", "sqlserver", "postgresql", "sqlite"));
        tagDTOS.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategory("DevTools");
        tool.setCategoryTags(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom emacs", "textmate", "hg"));
        tagDTOS.add(tool);

        return tagDTOS;

    }

    public static String filterInvalid(String tag) {
        String[] tagSplits = StringUtils.split(tag,",");
        List<TagDTO> tagDTOS = getTagDTOS();
        List<String> tagList = tagDTOS.stream().flatMap(t->t.getCategoryTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(tagSplits).filter(t->StringUtils.isBlank(t)|| !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
