package com.intcomex.rest.api.service.storage;

import com.intcomex.rest.api.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@Slf4j
@Profile("prod")
public class S3ImageStorageServiceImpl extends AbstractImageStorageService {

    public S3ImageStorageServiceImpl(AppProperties appProperties) {
        super(appProperties);
    }

    private S3Client getS3Client() {
        AppProperties.AwsProperties.S3Properties s3Props = appProperties.getAws().getS3();
        return S3Client.builder()
                .region(Region.of(s3Props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(s3Props.getAccessKeyId(), s3Props.getSecretAccessKey())))
                .build();
    }

    @Override
    public String storageImage(MultipartFile multipartFile, String categoryName) throws IOException {
        byte[] imageBytes = multipartFile.getBytes();
        String fileName = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        return storageImage(categoryName, fileName, imageBytes, contentType);
    }

    @Override
    public String storageImage(String categoryName, String fileName, byte[] imageBytes, String contentType) throws IOException {
        validateImage(imageBytes, contentType);

        String uniqueName = buildUniqueFileName(categoryName, fileName);
        String key = "categories/" + uniqueName;
        AppProperties.AwsProperties.S3Properties s3Props = appProperties.getAws().getS3();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Props.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        try (S3Client s3 = getS3Client()) {
            s3.putObject(request, RequestBody.fromBytes(imageBytes));
        }
        catch (Exception ex){
            log.error(">>>>>>>>>>>>>> Error S3 AWS", ex);
            throw ex;
        }
        return key;
    }

    public String getBaseUrlImage(){
        AppProperties.AwsProperties.S3Properties s3Props = appProperties.getAws().getS3();
        return s3Props.getUrlBase();
    }
}

