package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    PaymentInfo findByPaymentURL(String url);
    PaymentInfo findByTransakcija(Transakcija t);
}
