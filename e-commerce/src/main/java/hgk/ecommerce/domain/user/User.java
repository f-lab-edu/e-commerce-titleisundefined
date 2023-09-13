package hgk.ecommerce.domain.user;

import hgk.ecommerce.domain.user.dto.enums.Status;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(name = "login_id", length = 50, unique = true)
    private String loginId;

    @Column(length = 100)
    @NotNull
    private String password;

    @NotNull
    @Column(length = 50, unique = true)
    private String nickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Status status;

    @NotNull
    @Column(length = 100)
    private String address;

    public static User createUser(UserSignUpDto userSign) {
        User user = new User();

        user.loginId = userSign.getLoginId();
        user.password = userSign.getPassword();
        user.nickname = userSign.getNickname();
        user.address = userSign.getAddress();
        user.status = Status.ACTIVE;

        return user;
    }

    public void deleteUser() {
        this.status = Status.DELETE;
    }
}
