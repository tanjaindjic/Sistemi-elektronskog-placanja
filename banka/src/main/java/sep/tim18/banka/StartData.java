package sep.tim18.banka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class StartData {
    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @PostConstruct
    private void init(){
        Klijent klijent1 = new Klijent("ime1", "prez1", "01", "pass1", "mejl1", new ArrayList<>());
        Klijent klijent2 = new Klijent("ime2", "prez2", "02", "pass2", "mejl2", new ArrayList<>());
        Klijent klijent3 = new Klijent("ime3", "prez3", "03", "pass3", "mejl3", new ArrayList<>());
        klijentRepository.save(klijent1);
        klijentRepository.save(klijent2);
        klijentRepository.save(klijent3);

        Kartica kartica1 = new Kartica("123123123", "111", "01/25", "111222333", 100000000000F, 0F, klijent1 );
        Kartica kartica2 = new Kartica("234234234", "222", "01/25", "222333444", 100000000000F, 0F, klijent2 );
        Kartica kartica3 = new Kartica("345345345", "333", "01/25", "333444555", 100000000000F, 0F, klijent3 );
        karticaRepository.save(kartica1);
        karticaRepository.save(kartica2);
        karticaRepository.save(kartica3);

        klijent1.getKartice().add(kartica1);
        klijent2.getKartice().add(kartica2);
        klijent3.getKartice().add(kartica3);
        klijentRepository.save(klijent1);
        klijentRepository.save(klijent2);
        klijentRepository.save(klijent3);



    }

}
