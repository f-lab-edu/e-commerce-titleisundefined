package hgk.ecommerce.domain.storage.dto;

import hgk.ecommerce.domain.common.exception.InvalidRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
public class ImageSave {
    private MultipartFile multipartFile;
    private String originalName;
    private String virtualName;


    public ImageSave(MultipartFile multipartFile) {
        checkValid(multipartFile);
        this.multipartFile = multipartFile;
        this.originalName = multipartFile.getOriginalFilename();
        this.virtualName = UUID.randomUUID() + getExtension(originalName);
    }

    private void checkValid(MultipartFile multipartFile) {
        if(multipartFile == null) {
            throw new InvalidRequest("이미지는 필수로 등록해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        int lastIndex = multipartFile.getOriginalFilename().lastIndexOf(".");
        if(lastIndex == -1) {
            throw new InvalidRequest("파일의 확장자가 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private String getExtension(String fileName) {
        int lastIndex = multipartFile.getOriginalFilename().lastIndexOf(".");
        return fileName.substring(lastIndex);
    }
}
