package hgk.ecommerce.domain.owner.repository;

import hgk.ecommerce.domain.owner.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findOwnerByLoginId(String loginId);

    boolean existsOwnerByLoginId(String loginId);
}
