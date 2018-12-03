package sep.tim18.pcc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.repository.BankaRepository;

import javax.annotation.PostConstruct;
@PropertySource(ignoreResourceNotFound = true, value = "classpath:application.properties")
@Component
public class StartData {

    private static String B1URL;

    @Value("${B1URL}")
    public void setB1URLUrl(String bank1PaymentURL) {
        B1URL = bank1PaymentURL;
    }
    private static String B2URL;

    @Value("${B2URL}")
    public void setB2URL(String bank2PaymentURL) {
        B2URL = bank2PaymentURL;
    }
    private static String B1N;

    @Value("${B1N}")
    public void setB1URL(String bank1No) {
        B1N = bank1No;
    }
    private static String B2N;

    @Value("${B2N}")
    public void setB2N(String bank2No) {
        B2N = bank2No;
    }

    @Autowired
    private BankaRepository bankaRepository;

    @PostConstruct
    private void initData(){
        Banka b1 = new Banka();
        b1.setBrojBanke(B1N);
        b1.setUrlBanke(B1URL);
        bankaRepository.save(b1);

        Banka b2 = new Banka();
        b2.setBrojBanke(B2N);
        b2.setUrlBanke(B2URL);
        bankaRepository.save(b2);

    }
}
