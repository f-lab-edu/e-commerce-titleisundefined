package hgk.ecommerce.global.storage.dto;

import org.springframework.web.multipart.MultipartFile;

public interface IStorage {
    String getOriginalName();
    String getVirtualName();
}
