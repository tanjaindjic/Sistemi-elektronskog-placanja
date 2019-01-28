package sep.tim18.banka;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.PaymentInfoRepository;
import sep.tim18.banka.repository.TransakcijaRepository;


@PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
@Component
public class StartData {

    private static String BNumber;

    @Value("${BNumber}")
    public void setB1URL(String bank1No) {
        BNumber = bank1No;
    }

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;

    @Autowired
    private KarticaRepository karticaRepository;

    @Autowired
    private PaymentInfoRepository paymentInfoRepository;

    static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

    //@PostConstruct
    private void init(){
        Klijent klijent1 = new Klijent("prvi", "prvic", "1", "pass1", "mejl1", new ArrayList<>());
        Klijent klijent2 = new Klijent("drugi", "drugic", "2", "pass2", "mejl2", new ArrayList<>());
        Klijent klijent3 = new Klijent("treci", "trecic", "3", "pass3", "mejl3", new ArrayList<>());
        klijentRepository.save(klijent1);
        klijentRepository.save(klijent2);
        klijentRepository.save(klijent3);

        Kartica kartica1 = new Kartica(BNumber + "2233334444", "111", "1/25", BNumber + "001", 0F, 0F, klijent1 );
        Kartica kartica2 = new Kartica(BNumber + "3344445555", "222", "1/25", BNumber + "002", 10000F, 0F, klijent2 );
        Kartica kartica3 = new Kartica(BNumber + "4455556666", "333", "1/25", BNumber + "003", 10000F, 0F, klijent3 );
        karticaRepository.save(kartica1);
        karticaRepository.save(kartica2);
        karticaRepository.save(kartica3);

        klijent1.getKartice().add(kartica1);
        klijent2.getKartice().add(kartica2);
        klijent3.getKartice().add(kartica3);
        klijentRepository.save(klijent1);
        klijentRepository.save(klijent2);
        klijentRepository.save(klijent3);

        Transakcija transakcija1 = new Transakcija(null, klijent2, "1", new Date(System.currentTimeMillis()), Status.K,
                kartica1.getPan(), null, 100F, "succ", "fail", "error", 5L, new Date(System.currentTimeMillis()));
        transakcijaRepository.save(transakcija1);
        PaymentInfo paymentInfo = new PaymentInfo(transakcija1, "1");
        paymentInfoRepository.save(paymentInfo);
        Transakcija transakcija2 = new Transakcija(null, klijent2, "2",new Date(System.currentTimeMillis()  - (30 * ONE_MINUTE_IN_MILLIS)) , Status.K,
                kartica1.getPan(), null, 100F, "succ", "fail", "error", 5L, new Date(System.currentTimeMillis()  - (30 * ONE_MINUTE_IN_MILLIS)));
        transakcijaRepository.save(transakcija2);
        PaymentInfo paymentInfo2 = new PaymentInfo(transakcija2, "2");
        paymentInfoRepository.save(paymentInfo2);


    }

}
