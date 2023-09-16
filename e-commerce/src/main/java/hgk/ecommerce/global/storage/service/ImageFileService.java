package hgk.ecommerce.global.storage.service;

import hgk.ecommerce.domain.common.exceptions.InvalidRequest;
import hgk.ecommerce.domain.common.exceptions.NoResourceException;
import hgk.ecommerce.global.storage.ImageFile;
import hgk.ecommerce.global.storage.repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static hgk.ecommerce.global.storage.service.StorageService.Bucket.*;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final StorageService storageService;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public ImageFile saveImageFile(MultipartFile file) {
        String fileName = file.getName();
        ImageFile imageFile = ImageFile.createImageFile(fileName, createRandomName(fileName));

        storageService.uploadAsync(file, imageFile, ITEM_IMAGE);

        return imageFileRepository.save(imageFile);
    }

    @Transactional
    public ImageFile putImageFile(Long itemFileId, MultipartFile file) {
        String fileName = file.getName();

        ImageFile imageFile = getImageFileById(itemFileId);

        storageService.deleteAsync(imageFile, ITEM_IMAGE);
        imageFile.editImageFile(fileName, createRandomName(fileName));
        storageService.uploadAsync(file, imageFile, ITEM_IMAGE);

        return imageFile;
    }

    @Transactional
    public void deleteImageFile(Long imageFileId) {
        imageFileRepository.deleteById(imageFileId);
    }

    @Transactional(readOnly = true)
    public ImageFile getImageFileById(Long imageFileId) {
        return imageFileRepository.findById(imageFileId).orElseThrow(() -> {
            throw new NoResourceException("이미지를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        });
    }

    private String createRandomName(String fileName) {
        int extensionIndex = fileName.lastIndexOf(".");
        if(extensionIndex == -1) {
            throw new InvalidRequest("확장자가 없는 파일은 업로드 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return UUID.randomUUID() + fileName.substring(extensionIndex);
    }
}
