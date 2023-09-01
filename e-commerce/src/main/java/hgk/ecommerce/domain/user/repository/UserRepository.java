package hgk.ecommerce.domain.user.repository;

import hgk.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByLoginId(String loginId);
    boolean existsUserByNickname(String nickname);
    Optional<User> findUserByLoginId(String loginId);
}
