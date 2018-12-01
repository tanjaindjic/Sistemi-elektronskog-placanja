package sep.tim18.banka.serviceImpl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.RequestDTO;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.TransakcijaRepository;
import sep.tim18.banka.service.MainService;

import java.security.SecureRandom;
import java.sql.Date;

@Service
public class MainServiceImpl implements MainService {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Value("${tokenDuration}")
    private int tokenDuration;

    @Autowired
    private TransakcijaRepository transakcijaRepository;

    @Autowired
    private KlijentRepository klijentRepository;


    @Override
    public boolean validate(RequestDTO request) {
        return true;
        //TODO dopuniti metodu
    }

    @Override
    public String getToken() {
        StringBuilder sb = new StringBuilder( 50 );
        for( int i = 0; i < 50; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    @Override
    public Transakcija createTransaction(RequestDTO request) {
        //kreiram novu transakciju
        Transakcija t = new Transakcija();
        Klijent prodavac = klijentRepository.findByMerchantID(request.getMerchantID());
        //TODO skontati kako biramo na koju karticu tj. racun uplacujemo novac prodavcu jer u NC pamtimo samo njegov merchantID
        t.setRacunPrimaoca(prodavac.getKartice().get(0).getBrRacuna()); //po defaultu uzimam prvu karticu i na nju uplacujem
        t.setIznos(request.getIznos());
        t.setStatus(Status.C);
        t.setVremeKreiranja(new DateTime());
        String paymentURL = getToken();
        t.setPaymentURL(paymentURL);
        t.setRacunPosiljaoca(null);
        t.setVremeIzvrsenja(null);
        transakcijaRepository.save(t);

        return t;
    }

    @Override
    public boolean isTokenExpired(String token) {
        Transakcija t = transakcijaRepository.findByPaymentURL(token);
        if(t!=null){
            //TODO proveriti da li ovo radi kako treba
            if(t.getVremeKreiranja().plusMinutes(tokenDuration).isBeforeNow())
                return true;
            else
                return false;
        }else return true;
    }


}
