package hgk.ecommerce.domain.owner;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.user.dto.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "owners")
public class Owner extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id;

    @NotNull
    @Column(name = "login_id", length = 50, unique = true)
    private String loginId;

    @NotNull
    @Column(length = 50)
    private String password;

    public static Owner createOwner(OwnerSign ownerSign) {
        Owner owner = new Owner();
        owner.loginId = ownerSign.getLoginId();
        owner.password = ownerSign.getPassword();
        return owner;
    }
}
