package hgk.ecommerce.domain.payment.service;

import hgk.ecommerce.domain.cart.repository.CartItemRepository;
import hgk.ecommerce.domain.cart.repository.CartRepository;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.payment.Payment;
import hgk.ecommerce.domain.payment.repository.PaymentRepository;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PaymentService paymentService;
    @PersistenceContext
    EntityManager em;

    User user;

    @BeforeEach
    void beforeEach() {
        user = createUser("test-user", "test-password");
    }

    @Test
    void 포인트_충전() {
        int chargePoint = 10000;
        paymentService.increasePoint(user, chargePoint);

        flushAndClearPersistence();

        Payment payment = paymentRepository.findPaymentByUserId(user.getId()).get();

        assertThat(payment.getPoint()).isEqualTo(chargePoint);
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void 포인트_감소() {
        int chargePoint = 10000;
        paymentService.increasePoint(user, chargePoint);

        flushAndClearPersistence();

        Payment payment = paymentRepository.findPaymentByUserId(user.getId()).get();
        assertThat(payment.getPoint()).isEqualTo(chargePoint);

        int dischargePoint = 5000;

        paymentService.decreasePoint(user, dischargePoint);
        flushAndClearPersistence();

        payment = paymentRepository.findPaymentByUserId(user.getId()).get();
        assertThat(payment.getPoint()).isEqualTo(chargePoint- dischargePoint);
        assertThat(payment.getUser().getId()).isEqualTo(user.getId());
    }

    //region PRIVATE METHOD

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private User createUser(String loginId, String password) {
        UserSignUpDto userSign = UserSignUpDto.builder()
                .loginId(loginId)
                .password(password)
                .address(loginId + "'s address'")
                .nickname(loginId + "'s nick")
                .build();
        User user = User.createUser(userSign);
        return userRepository.save(user);
    }

    //endregion
}