package hgk.ecommerce.domain.owner;

import hgk.ecommerce.domain.common.entity.BaseTimeEntity;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
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
public class Owner extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id;

    @NotNull
    @Column(name = "login_id", length = 50, unique = true)
    private String loginId;

    @NotNull
    @Column(length = 100)
    private String password;

    public static Owner createOwner(OwnerSignUpDto ownerSign) {
        Owner owner = new Owner();
        owner.loginId = ownerSign.getLoginId();
        owner.password = ownerSign.getPassword();

        return owner;
    }
}
