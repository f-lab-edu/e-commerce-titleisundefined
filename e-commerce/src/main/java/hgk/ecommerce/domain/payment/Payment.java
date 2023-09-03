package hgk.ecommerce.domain.payment;

import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private Integer point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Payment createPayment(User user) {
        Payment payment = new Payment();
        payment.point = 0;
        payment.user = user;

        return payment;
    }

    public void increasePoint(int point) {
        this.point += point;
    }

    public void decreasePoint(int point) {
        if(this.point - point < 0) {
            throw new NoResourceException("포인트가 부족합니다.", HttpStatus.BAD_REQUEST);
        }
        this.point -= point;
    }
}
