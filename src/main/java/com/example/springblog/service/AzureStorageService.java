package com.example.springblog.service;

import com.example.springblog.dto.AzureStorageParam;
import com.example.springblog.dto.FileDTO;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AzureStorageService{
    // 设置缩略图的宽高

    @Value("{azure.storage.generate-thumbnail}")
    private String generateThumbnail;

    @Autowired
    private AzureStorageParam azureStorageParam;

    public CloudBlobContainer getCloudBlobContainer(){
        String storageConnectionString = azureStorageParam.getStorageConnectionString();
        CloudStorageAccount storageAccount;
        CloudBlobClient blobClient = null;
        CloudBlobContainer container = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference(azureStorageParam.getContainerReference());
        } catch (URISyntaxException | StorageException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return container;


    }


    //@Override
    public static String uploadFile(CloudBlobContainer container, String filePath) {
        // 获取blob容器

        if (container == null) {
            throw new CustomizeException(CustomizeErrorCode.GET_CONTAINER_FAILURE);
        }

        FileInputStream fileInputStream = null;
        CloudBlockBlob blob = null;
        try {
            File source = new File(filePath);
            blob = container.getBlockBlobReference(source.getName());
            fileInputStream = new FileInputStream(source);
            blob.upload(fileInputStream, source.length());
        } catch (URISyntaxException | StorageException | FileNotFoundException e) {
            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
        } catch (IOException e) {
            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
        } catch (Exception e) {
            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
            }
        }
        String returnValue = null;
        if (blob != null) {
            returnValue = blob.getUri().toString();
        }
        return returnValue;
    }

    public static void downloadBlob(CloudBlobContainer container, String filePath, String blobName) {

        if (container == null) {
            throw new CustomizeException(CustomizeErrorCode.GET_CONTAINER_FAILURE);
        }

        FileOutputStream fileOutputStream = null;
        try {
            for (ListBlobItem blobItem : container.listBlobs()) {
                if (blobItem instanceof CloudBlob) {
                    CloudBlob blob = (CloudBlob) blobItem;
                    if (blob.getName().equals(blobName)) {
                        fileOutputStream = new FileOutputStream(filePath + blob.getName());
                        blob.download(fileOutputStream);
                    }
                }
            }
        } catch (FileNotFoundException | StorageException e) {
            throw new CustomizeException(CustomizeErrorCode.DOWNLOAD_FAILURE);
        } catch (IOException e) {
            throw new CustomizeException(CustomizeErrorCode.DOWNLOAD_FAILURE);
        } catch (Exception e) {
            throw new CustomizeException(CustomizeErrorCode.DOWNLOAD_FAILURE);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                throw new CustomizeException(CustomizeErrorCode.DOWNLOAD_FAILURE);
            }
        }
    }

    public static boolean deleteBlob(CloudBlobContainer container, String blobName) {
        boolean success = false;
        try {
            CloudBlockBlob blob = container.getBlockBlobReference(blobName);
            success = blob.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            throw new CustomizeException(CustomizeErrorCode.DELETE_FAILURE);
        } catch (Exception e) {
            throw new CustomizeException(CustomizeErrorCode.DELETE_FAILURE);
        }
        return success;
    }



    /**
     * 判断批量文件中是否都为图片
     */
    private boolean hasInvalidPic(MultipartFile[] multipartFiles) {
        List<String> picTypeList = Arrays.asList("image/jpg", "image/jpeg", "image/png");
        return Arrays.stream(multipartFiles).anyMatch(i -> !picTypeList.contains(i.getContentType().toLowerCase()));
    }
}
