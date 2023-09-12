package hgk.ecommerce.global.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import hgk.ecommerce.domain.common.exception.InternalServerException;
import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.ItemImage;
import hgk.ecommerce.domain.storage.dto.ImageSave;
import hgk.ecommerce.domain.storage.repository.ItemImageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-storage.properties")
public class NaverObjectStorage {
    private final ItemImageRepository itemImageRepository;
    private final List<String> validTypeList = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
    private AmazonS3 s3;

    @Value("${naver.storage.endpoint}")
    private String endPoint;
    @Value("${naver.storage.region.name}")
    private String regionName;
    @Value("${naver.storage.access.key}")
    private String accessKey;
    @Value("${naver.storage.secret.key}")
    private String secretKey;
    @Value("${naver.storage.item.bucket}")
    private String itemBucket;


    @PostConstruct
    private void postConstruct() {
        s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }


    public ItemImage uploadItemImage(ImageSave imageSave, Item item) {
        uploadFile(imageSave, itemBucket);
        return itemImageRepository.save(ItemImage.createItemImage(imageSave, item));
    }

    public void deleteItemFile(String fileName) {
        removeFile(itemBucket, fileName);
    }

    @Async
    protected void uploadFile(ImageSave imageSave, String bucket) {
        MultipartFile file = imageSave.getMultipartFile();
        String virtualName = imageSave.getVirtualName();

        try {
            String mediaType = new Tika().detect(file.getInputStream());
            checkMediaType(mediaType);

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(mediaType);

            PutObjectRequest objectRequest = new PutObjectRequest(bucket, virtualName, file.getInputStream(), metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(objectRequest);

        } catch (IOException e) {
            throw new InternalServerException("이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void uploadFile(MultipartFile imageSave, String bucket) {
        MultipartFile file = imageSave;
        String virtualName = UUID.randomUUID().toString();

        try {
            String mediaType = new Tika().detect(file.getInputStream());
            checkMediaType(mediaType);

            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentType(mediaType);

            PutObjectRequest objectRequest = new PutObjectRequest(bucket, virtualName, file.getInputStream(), metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(objectRequest);

        } catch (IOException e) {
            throw new InternalServerException("이미지 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Async
    protected void removeFile(String bucketName, String fileName) {
        s3.deleteObject(bucketName, fileName);
    }

    private void checkMediaType(String mediaType) {
        if (!validTypeList.contains(mediaType)) {
            throw new InvalidRequest("지원하지 않는 확장자 입니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
