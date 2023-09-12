package hgk.ecommerce.global.storage;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
public class UploadFile {
    private MultipartFile file;
    private String originalFileName;
    private String convertedFileName;

    public UploadFile(MultipartFile file) {
        this.file = file;
        this.originalFileName = file.getOriginalFilename();
        this.convertedFileName = createConvertedFileName();
    }

    private String createConvertedFileName() {
        String fileName = this.file.getOriginalFilename();
        int extensionIDx = fileName.lastIndexOf(".");
        if(extensionIDx == -1) {
            return "";
        }
        return UUID.randomUUID() + fileName.substring(extensionIDx);
    }
}
