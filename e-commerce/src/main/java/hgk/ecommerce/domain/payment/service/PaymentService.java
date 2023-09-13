package hgk.ecommerce.domain.payment.service;

import hgk.ecommerce.domain.payment.Payment;
import hgk.ecommerce.domain.payment.repository.PaymentRepository;
import hgk.ecommerce.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void increasePoint(User user, Integer point) {
        Payment payment = getPaymentByUser(user);

        payment.increasePoint(point);
    }

    @Transactional
    public void decreasePoint(User user, Integer point) {
        Payment payment = getPaymentByUser(user);
        payment.decreasePoint(point);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Payment createPayment(User user) {
        Payment payment = Payment.createPayment(user);

        return paymentRepository.save(payment);
    }

    //region PRIVATE METHOD

    private Payment getPaymentByUser(User user) {
        Optional<Payment> optionalPayment = paymentRepository.findPaymentByUserId(user.getId());
        if(optionalPayment.isEmpty()) {
            return createPayment(user);
        }
        return optionalPayment.get();
    }

    //endregion
}
