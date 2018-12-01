package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.Klijent;

public interface KlijentRepository extends JpaRepository<Klijent, Long>{
    Klijent findByMerchantID(String id);
    Klijent findByKartice_pan(String pan);
}
