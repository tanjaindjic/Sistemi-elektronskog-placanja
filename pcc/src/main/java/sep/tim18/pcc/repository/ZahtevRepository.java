package sep.tim18.pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.enums.Status;

import java.util.List;

public interface ZahtevRepository extends JpaRepository<Zahtev, Long>{
    List<Zahtev> findByAcquirerOrderID(Long id);
    Zahtev findByAcquirerOrderIDAndStatus(Long acquirerOrderID, Status k);
}
