package com.example.springblog.advice;

import com.alibaba.fastjson.JSON;
import com.example.springblog.dto.ResultDTO;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ModelAndView handleControllerException(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Throwable ex, Model model) {

        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException){
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            }else{
                resultDTO =  ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try{
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(200);
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            }catch(IOException e){

            }
            return null;
        }
        else{
            HttpStatus status = getStatus(request);
            if (ex instanceof CustomizeException){
                model.addAttribute("message", ex.getMessage());
            }else{
                model.addAttribute("message",CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }

    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
