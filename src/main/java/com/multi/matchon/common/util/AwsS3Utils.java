package com.multi.matchon.common.util;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


import java.io.*;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Utils {

    private final S3Operations s3Operations;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;



    public void saveFile(String dirName, String fileName, MultipartFile multipartFile) {

        // String replaceFileName = fileName + "." + FilenameUtils.getExtension(multipartFile.getResource().getFilename());

        String s3Key = dirName + fileName + "." + FilenameUtils.getExtension(multipartFile.getOriginalFilename());;

        System.out.println("======>    "+s3Key);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ObjectMetadata metadata = ObjectMetadata.builder()
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Operations.upload(bucket, s3Key, inputStream, metadata);
            // 2. 업로드 후 ACL을 public-read로 변경
            PutObjectAclRequest aclRequest = PutObjectAclRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObjectAcl(aclRequest); //개별권한설정
            log.info("[AwsS3Utils] File Uploaded Successfully: " + s3Key);

            //return replaceFileName;

        } catch (IOException e) {
            log.error("[AwsS3Utils] File Upload Failed: " + s3Key, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
    }


    public S3Resource downloadFile(String dirName, String savedName) throws IOException {
        //String savedNameOnly = savedName.substring(0,savedName.indexOf(".")); //확장자 제거
        S3Resource resource =  s3Operations.download(bucket,dirName+savedName);

//       ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resource.getOutputStream();
//       byte[] data = outputStream.toByteArray();
//       Path filePath = Paths.get("C:/temp/test.pdf");
//       Files.write(filePath, data);
//       log.info("로컬에 복사 완료");

        /////////////////////////////
//        File targetFile = new File("C:/temp/reservation.pdf"); // 원하는 로컬 경로
//
//        try (InputStream inputStream = resource.getInputStream();
//             OutputStream outputStream = new FileOutputStream(targetFile)) {
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            System.out.println("저장 완료!");
//        }

        return resource;
    }

    /* Create a pre-signed URL to download an object in a subsequent GET request. */ //임시주석 전준혁
//    public String createPresignedGetUrl(String dirName, String savedName) {
//
//        // String savedNameOnly = savedName.substring(0,savedName.indexOf(".")); //확장자 제거
//
//
//        try (S3Presigner presigner = S3Presigner.create()) {
//
//            GetObjectRequest objectRequest = GetObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(dirName+savedName)
//                    .build();
//
//            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
//                    .getObjectRequest(objectRequest)
//                    .build();
//
//            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
//            //log.info("Presigned URL: [{}]", presignedRequest.url().toString());
//            //log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
//
//            return presignedRequest.url().toExternalForm();
//        }
//    }

    public String createPresignedGetUrl(String dirName, String savedName) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                System.getenv("AWS_ACCESS_KEY"),
                System.getenv("AWS_SECRET_KEY")
        );

        try (S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .build()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(dirName + savedName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toExternalForm();
        }
    }

    /**
     * S3에서 파일 삭제
     */
    public boolean deleteFile(String dirName, String fileName) {
        log.info("[AwsS3Utils] deleteFile Start =====================");
        String s3Key = dirName + fileName; // yml 에서  products 까지 경로 줘도되고, 이미지 업로드 구분하려면 폴더명을 서비스에서 받아서 이용
        System.out.println("======>    "+s3Key);

        try {
            s3Operations.deleteObject(bucket, s3Key);
            log.info("[AwsS3Utils] File Deleted Successfully: " + s3Key);
            return true;
        } catch (Exception e) {
            log.error("[AwsS3Utils] File Deletion Failed: " + s3Key, e);
            return false;
        }
    }

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String getObjectUrl(String dir, String filename, MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + dir + filename + "." + extension;
    }


    /**
     * 저장 경로 전체를 인자로 받아 다운로드 (예: "community/파일명.ext")
     */
    public S3Resource downloadFileWithFullName(String fullPath) throws IOException {
        return s3Operations.download(bucket, fullPath);
    }






}
