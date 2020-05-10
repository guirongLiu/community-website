package com.example.springblog.controller;

import com.example.springblog.dto.FileDTO;
import com.example.springblog.provider.AzureProvider;
import com.example.springblog.service.AzureStorageService;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class FileController {

    @Autowired
    AzureProvider azureProvider;

    @RequestMapping(path = "/file/upload",produces = "application/json; charset=UTF-8")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String url = azureProvider.uploadMultiPartFile(file,file.getContentType(),file.getOriginalFilename());
        FileDTO fileDTO = new FileDTO();
        if(url != null || url.length()>0) {
            fileDTO.setSuccess(1);
            fileDTO.setUrl(url);
            return fileDTO;
        }
        return null;
    }


}
