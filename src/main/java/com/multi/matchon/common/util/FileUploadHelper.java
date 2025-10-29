package com.multi.matchon.common.util;

import com.multi.matchon.common.dto.UploadedFile;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileUploadHelper {

    public static UploadedFile uploadToS3(MultipartFile file, String dirName, AwsS3Utils awsS3Utils) {
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String uuidFileName = UUID.randomUUID() + "_" + baseName;
        String fullFileName = uuidFileName + "." + extension;

        awsS3Utils.saveFile(dirName, uuidFileName, file);

        return new UploadedFile(fullFileName, originalFilename);
    }
}

