package sep.tim18.pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.pcc.model.Zahtev;

public interface ZahtevRepository extends JpaRepository<Zahtev, Long>{
    Zahtev findByAcquirerOrderID(Long id);
}
