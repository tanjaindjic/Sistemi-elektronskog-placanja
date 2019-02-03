package sep.tim18.pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.pcc.model.PCCRequest;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.enums.Status;

import java.util.List;

public interface PCCRequestRepository extends JpaRepository<PCCRequest, Long> {
    List<PCCRequest> findByAcquirerOrderID(Long id);
    PCCRequest findByMerchantOrderID(Long id);
}
