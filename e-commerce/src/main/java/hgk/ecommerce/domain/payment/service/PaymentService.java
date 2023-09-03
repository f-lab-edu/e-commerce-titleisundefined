package hgk.ecommerce.domain.payment.service;

import hgk.ecommerce.domain.common.exception.InvalidRequest;
import hgk.ecommerce.domain.payment.Payment;
import hgk.ecommerce.domain.payment.repository.PaymentRepository;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void chargePoint(User user, Integer point) {
        Payment payment = getPaymentByUser(user);
        if(!mockAuth()) {
            throw new InvalidRequest("인증에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
        payment.increasePoint(point);
    }

    @Transactional
    public void decreasePoint(User user, Integer point) {
        Payment payment = getPaymentByUser(user);
        payment.decreasePoint(point);
    }

    @Transactional
    public Payment getPaymentByUser(User user) {
        Payment payment = paymentRepository.findPaymentByUser(user).orElse(null);
        if(payment == null) {
            payment = Payment.createPayment(user);
            return paymentRepository.save(payment);
        }
        return payment;
    }

    //region PRIVATE METHOD
    private boolean mockAuth() {
        return true;
    }
    //endregion
}
