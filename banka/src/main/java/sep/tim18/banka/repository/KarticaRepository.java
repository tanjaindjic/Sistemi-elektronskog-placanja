package sep.tim18.banka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep.tim18.banka.model.Kartica;

public interface KarticaRepository extends JpaRepository<Kartica, Long> {
}
