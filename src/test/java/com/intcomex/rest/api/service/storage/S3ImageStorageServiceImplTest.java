package com.intcomex.rest.api.service.storage;

import com.intcomex.rest.api.config.AppProperties;
import com.intcomex.rest.api.config.AppProperties.AwsProperties;
import com.intcomex.rest.api.config.AppProperties.AwsProperties.S3Properties;
import com.intcomex.rest.api.config.AppProperties.ImagesCategory;
import com.intcomex.rest.api.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
class S3ImageStorageServiceImplTest {

    @InjectMocks
    private S3ImageStorageServiceImpl storageService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AwsProperties awsProperties;

    @BeforeEach
    void setUp() {
        ImagesCategory imagesCategory = new ImagesCategory();
        imagesCategory.setImagePublicPath("/");
        imagesCategory.setFormatImages(List.of("image/png", "image/jpeg"));
        imagesCategory.setSizeImageCat(10485760);
        when(appProperties.getImagesCategory()).thenReturn(imagesCategory);
    }

    @Test
    void shouldUploadImageToS3Successfully() throws IOException {
        byte[] imageBytes = "contenido de prueba".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "foto.png", "image/png", imageBytes
        );

        S3Properties s3Properties = new S3Properties();
        s3Properties.setBucket("bucket-de-prueba");
        s3Properties.setAccessKeyId("access-key");
        s3Properties.setSecretAccessKey("secret-key");
        s3Properties.setRegion("us-east-1");
        s3Properties.setUrlBase("https://s3.amazonaws.com/bucket-de-prueba/");
        when(appProperties.getAws()).thenReturn(awsProperties);
        when(awsProperties.getS3()).thenReturn(s3Properties);

        try (MockedStatic<S3Client> s3ClientStatic = mockStatic(S3Client.class)) {
            S3ClientBuilder builder = mock(S3ClientBuilder.class);
            S3Client mockS3Client = mock(S3Client.class);

            s3ClientStatic.when(S3Client::builder).thenReturn(builder);
            when(builder.region(Region.of("us-east-1"))).thenReturn(builder);
            when(builder.credentialsProvider(any(StaticCredentialsProvider.class))).thenReturn(builder);
            when(builder.build()).thenReturn(mockS3Client);

            when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .thenReturn(PutObjectResponse.builder().build());

            String result = storageService.storageImage(file, "SERVIDORES");

            assertNotNull(result);
            assertTrue(result.startsWith("categories/"));
            verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }
    }

    @Test
    void shouldRejectInvalidContentTypeInBytes() {
        byte[] content = "archivo no válido".getBytes();
        String fileName = "manual.pdf";
        String invalidContentType = "application/pdf";

        BusinessException ex = assertThrows(BusinessException.class, () ->
                storageService.storageImage("DOCS", fileName, content, invalidContentType)
        );
    }
}
