package hgk.ecommerce.global.storage.repository;

import hgk.ecommerce.global.storage.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

}
