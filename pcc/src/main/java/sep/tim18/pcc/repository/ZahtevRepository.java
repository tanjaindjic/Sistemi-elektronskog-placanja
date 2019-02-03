package sep.tim18.pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.enums.Status;

import java.util.List;

public interface ZahtevRepository extends JpaRepository<Zahtev, Long>{
    Zahtev findByAcquirerOrderID(Long id);
    List<Zahtev> findByStatus(Status s);
    Zahtev findByAcquirerOrderIDAndStatus(Long acquirerOrderID, Status k);
    Zahtev findByMerchantOrderId(Long id);

}
