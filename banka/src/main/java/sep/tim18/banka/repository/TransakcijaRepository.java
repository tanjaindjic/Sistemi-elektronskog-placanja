package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.enums.Status;

import java.util.List;

public interface TransakcijaRepository extends JpaRepository<Transakcija, Long>{
    List<Transakcija> findByStatus(Status s);

}
