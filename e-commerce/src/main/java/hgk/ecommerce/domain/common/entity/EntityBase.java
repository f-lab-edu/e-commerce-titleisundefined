package hgk.ecommerce.domain.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class EntityBase {
    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;
}
