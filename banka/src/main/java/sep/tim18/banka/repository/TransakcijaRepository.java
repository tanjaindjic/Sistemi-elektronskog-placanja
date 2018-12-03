package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.Transakcija;

public interface TransakcijaRepository extends JpaRepository<Transakcija, Long>{
}
