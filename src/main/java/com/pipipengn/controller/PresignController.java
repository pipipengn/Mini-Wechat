package com.pipipengn.controller;

import com.pipipengn.utils.AWS;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class PresignController {

    @GetMapping("/S3Url")
    public Map<String, String> getS3PreSignedURL() {
        Map<String, String> map = new HashMap<>();
        map.put("url", "");

        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(AWS.AccessKey, AWS.AccessSecret);
            AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

            S3Presigner preSigner = S3Presigner.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.US_WEST_2).build();

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(AWS.S3BucketName)
                    .key(UUID.randomUUID() + ".jpg")
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = preSigner.presignPutObject(presignRequest);


            String myURL = presignedRequest.url().toString();
            map.put("url", myURL);

        } catch (S3Exception e) {
            e.getStackTrace();
        }
        return map;
    }
}
