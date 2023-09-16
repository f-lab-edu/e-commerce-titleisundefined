package hgk.ecommerce.global.storage.service;

import hgk.ecommerce.global.storage.dto.IStorage;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;


public interface StorageService {
    @Getter
    enum Bucket {
        ITEM_IMAGE("/item-image");

        private final String bucketName;

        Bucket(String bucketName) {
            this.bucketName = bucketName;
        }
    };

    @Async
    void uploadAsync(MultipartFile file, IStorage iStorage, Bucket bucket);
    @Async
    void deleteAsync(IStorage iStorage, Bucket bucket);
}
