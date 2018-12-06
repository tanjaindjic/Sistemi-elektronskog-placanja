package sep.tim18.pcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.pcc.model.Banka;

public interface BankaRepository extends JpaRepository<Banka, Long>{
    Banka findByBrojBanke(String s);
}
