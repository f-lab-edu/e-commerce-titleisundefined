package hgk.ecommerce.global.storage.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import hgk.ecommerce.domain.common.exceptions.InvalidRequest;
import hgk.ecommerce.global.storage.dto.IStorage;
import jakarta.annotation.PostConstruct;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Profile("release")
public class NaverStorageService implements StorageService{
    private AmazonS3 amazonS3;
    private final List<String> validTypeList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");

    @Value("${naver.storage.endpoint}")
    private String endPoint;
    @Value("${naver.storage.region}")
    private String region;
    @Value("${naver.storage.accesskey}")
    private String accessKey;
    @Value("${naver.storage.secretkey}")
    private String secretKey;
    @Value("${naver.storage.item.bucket}")
    private String itemBucket;

    @PostConstruct
    private void postConstruct() {
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    @Override
    public void upload(MultipartFile file, IStorage iStorage, Bucket bucket) {
        uploadFile(file, iStorage, bucket);
    }

    @Override
    public void delete(IStorage iStorage, Bucket bucket) {

    }

    @Async
    protected void uploadFile(MultipartFile file, IStorage iStorage, Bucket bucket) {
        String virtualName = iStorage.getVirtualName();
        String bucketName = bucket.getBucketName();

        try {
            String mediaType = new Tika().detect(file.getInputStream());
            checkMediaType(mediaType);

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(mediaType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, virtualName, file.getInputStream(), metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deleteFile(IStorage iStorage, Bucket bucket) {
        amazonS3.deleteObject(bucket.getBucketName(), iStorage.getVirtualName());
    }

    private void checkMediaType(String mediaType) {
        if(!validTypeList.contains(mediaType)) {
            throw new InvalidRequest("지원하지 않는 확장자입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
