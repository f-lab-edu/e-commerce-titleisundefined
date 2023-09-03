package hgk.ecommerce.domain.payment.repository;

import hgk.ecommerce.domain.payment.Payment;
import hgk.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByUser(User user);
}
