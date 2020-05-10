package com.example.springblog.provider;

import com.example.springblog.dto.AzureStorageParam;
import com.example.springblog.exception.CustomizeErrorCode;
import com.example.springblog.exception.CustomizeException;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

@Service
public class AzureProvider {
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
    public String uploadMultiPartFile(MultipartFile multipartFile, String mimeType, String fileName) {
        // 获取blob容器
        CloudBlobContainer container = getCloudBlobContainer();
        if (container == null) {
            throw new CustomizeException(CustomizeErrorCode.GET_CONTAINER_FAILURE);
        }

        String generatedFileName;
        int index = fileName.lastIndexOf(".");

        if (index > 0) {
            generatedFileName = UUID.randomUUID().toString() + fileName.substring(index);
        } else {
            return null;
        }
        String fileUrl = "";
        File tempFile = null;
        try {
            tempFile = File.createTempFile(fileName.substring(0, index), fileName.substring(index));
            tempFile.deleteOnExit();
            multipartFile.transferTo(tempFile);
            fileUrl = uploadFile(container,tempFile,mimeType,generatedFileName);
            // tidy up
            tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileUrl);
        return fileUrl;

    }

    public String uploadFile(CloudBlobContainer container, File file, String mimeType, String fileName) {
        FileInputStream fileInputStream = null;
        CloudBlockBlob blob = null;
        try {

            blob = container.getBlockBlobReference(fileName);
            fileInputStream  = new FileInputStream(file);
            System.out.println(blob.getName());
            blob.upload(fileInputStream, file.length());

        } catch (URISyntaxException | StorageException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
        } catch (IOException e) {
            System.out.println(e.getMessage());

            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FAILURE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            returnValue = getSasUrl(fileName);
        }
        return returnValue;
    }

    public void downloadBlob( String filePath, String blobName) {
        CloudBlobContainer container = getCloudBlobContainer();
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

    public boolean deleteBlob(String blobName) {
        CloudBlobContainer container = getCloudBlobContainer();
        if (container == null) {
            throw new CustomizeException(CustomizeErrorCode.GET_CONTAINER_FAILURE);
        }

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


    public String getSasUrl(String fileName){
        CloudBlobContainer container = getCloudBlobContainer();
        if (container == null) {
            throw new CustomizeException(CustomizeErrorCode.GET_CONTAINER_FAILURE);
        }
//        BlobContainerPermissions permissions = new BlobContainerPermissions();
////
////        // define a Read-only base policy for downloads
////        SharedAccessBlobPolicy readPolicy = new SharedAccessBlobPolicy();
////        readPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));
////        permissions.getSharedAccessPolicies().put("DownloadPolicy", readPolicy);
////
////        try {
////            container.uploadPermissions(permissions);
////        } catch (StorageException e) {
////            e.printStackTrace();
////        }

        // define rights you want to bake into the SAS
        SharedAccessBlobPolicy itemPolicy = new SharedAccessBlobPolicy();

        // calculate Start Time
        LocalDateTime now = LocalDateTime.now();
        // SAS applicable as of 15 minutes ago
        Instant result = now.minusMinutes(15).atZone(ZoneOffset.UTC).toInstant();
        Date startTime = Date.from(result);

        // calculate Expiration Time
        now = LocalDateTime.now();
        result = now.plusDays(7).atZone(ZoneOffset.UTC).toInstant();
        Date expirationTime = Date.from(result);

        itemPolicy.setSharedAccessStartTime(startTime);
        itemPolicy.setSharedAccessExpiryTime(expirationTime);

        // get reference to the Blob you want to generate the SAS for:
        CloudBlockBlob blob = null;
        try {
            blob = container.getBlockBlobReference(fileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        // generate Download SAS
        String sasToken = null;
        try {
            sasToken = blob.generateSharedAccessSignature(itemPolicy, "DownloadPolicy");
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        // the SAS URL is actually concatentation of the blob URI and the generated token:
        String sasUri = String.format("%s?%s", blob.getUri(), sasToken);
        return sasUri;
    }

}