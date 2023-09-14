package hgk.ecommerce.global.storage.service;

import hgk.ecommerce.global.storage.dto.IStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("test")
public class MockStorageService implements StorageService{

    @Override
    public void upload(MultipartFile file, IStorage iStorage, Bucket bucket) {
        uploadFile(file, iStorage, bucket);
    }

    @Override
    public void delete(IStorage iStorage, Bucket bucket) {
        deleteFile(iStorage, bucket);
    }

    @Async
    protected void uploadFile(MultipartFile file, IStorage iStorage, Bucket bucket) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    protected void deleteFile(IStorage iStorage, Bucket bucket) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
