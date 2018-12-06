package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.PaymentInfo;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    PaymentInfo findByPaymentURL(String url);
}
