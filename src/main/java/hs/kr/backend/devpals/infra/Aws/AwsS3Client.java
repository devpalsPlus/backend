package hs.kr.backend.devpals.infra.aws;

import hs.kr.backend.devpals.global.exception.CustomException;
import hs.kr.backend.devpals.global.exception.ErrorException;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class AwsS3Client {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile file, String fileName) {

        if (file.isEmpty()) {
            throw new CustomException(ErrorException.FILE_EMPTY);
        }

        try (InputStream is = file.getInputStream()) {
            S3Resource upload = s3Template.upload(bucketName, fileName, is);

            return upload.getURL().toString();
        } catch (IOException | S3Exception e) {
            throw new CustomException(ErrorException.FAIL_UPLOAD);
        }
    }


    public void delete(String fileName) {
        try {
            s3Template.deleteObject(bucketName, fileName);
        } catch (S3Exception e) {
            throw new CustomException(ErrorException.FILE_NOT_SEARCH);
        }
    }

    // 파일 타입 검증
    public boolean isValidImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") ||
                lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png");
    }
}
