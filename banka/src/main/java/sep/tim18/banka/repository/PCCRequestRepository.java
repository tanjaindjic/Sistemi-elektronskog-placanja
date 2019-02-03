package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.PCCRequest;

public interface PCCRequestRepository extends JpaRepository<PCCRequest, Long> {
    PCCRequest findByMerchantOrderID(Long id);
}
