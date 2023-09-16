package hgk.ecommerce.global.storage;

import hgk.ecommerce.domain.common.entity.BaseTimeEntity;
import hgk.ecommerce.global.storage.dto.IStorage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image_file")
@EqualsAndHashCode(of = "id", callSuper = true)
@Getter
public class ImageFile extends BaseTimeEntity implements IStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_file_id")
    private Long id;

    @Column(nullable = false, name = "original_name", length = 100)
    private String originalName;

    @Column(nullable = false, name = "virtual_name", length = 50)
    private String virtualName;

    public static ImageFile createImageFile(String originalName, String virtualName) {
        ImageFile imageFile = new ImageFile();
        imageFile.virtualName = virtualName;
        imageFile.originalName = originalName;

        return imageFile;
    }

    public void editImageFile(String originalName, String virtualName) {
        this.originalName = originalName;
        this.virtualName = virtualName;
    }

    @Override
    public String getOriginalName() {
        return originalName;
    }

    @Override
    public String getVirtualName() {
        return virtualName;
    }
}
