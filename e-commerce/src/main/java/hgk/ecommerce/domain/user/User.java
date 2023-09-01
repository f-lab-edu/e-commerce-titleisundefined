package hgk.ecommerce.domain.user;

import hgk.ecommerce.domain.common.entity.EntityBase;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.dto.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static hgk.ecommerce.domain.user.dto.enums.Status.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "login_id", length = 50, unique = true)
    private String loginId;

    @Column(length = 50)
    @NotNull
    private String password;

    @NotNull
    @Column(length = 50, unique = true)
    private String nickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Status status;

    @NotNull
    @Column(length = 50)
    private String address;

    public static User createUser(UserSign userSign) {
        User user = new User();

        user.loginId = userSign.getLoginId();
        user.password = userSign.getPassword();
        user.nickname = userSign.getNickname();
        user.address = userSign.getAddress();
        user.status = ACTIVE;

        return user;
    }

    public void deleteUser() {
        this.status = DELETED;
    }
}
