package hgk.ecommerce.global.storage.repository;

import hgk.ecommerce.global.storage.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
