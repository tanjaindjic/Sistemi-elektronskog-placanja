package sep.tim18.banka.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import sep.tim18.banka.model.Kartica;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.BuyerInfoDTO;
import sep.tim18.banka.model.dto.KPRequestDTO;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.repository.KarticaRepository;
import sep.tim18.banka.repository.KlijentRepository;
import sep.tim18.banka.repository.PaymentInfoRepository;
import sep.tim18.banka.repository.TransakcijaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class AcquirerControllerIntegrationTests {
	@Autowired
    private MockMvc mvc; 
    @Autowired 
    private PaymentInfoRepository paymentInfoRepository;
    @Autowired 
    private TransakcijaRepository transakcijaRepository;   
    @Autowired
    private KlijentRepository klijentRepository;
    @Autowired
    private KarticaRepository karticaRepository;
    
    private static String BNumber;

    @Value("${BNumber}")
    public void setB1URL(String bank1No) {
        BNumber = bank1No;
    }
    
    private KPRequestDTO obj;
    private Transakcija transakcija;
    private PaymentInfo paymentInfo;
    private Klijent primalac;
    private Kartica karticaP;
    private Klijent uplatilac;
    private Kartica karticaU;
	
    @PostConstruct
    public void setUp() {

    	obj = new KPRequestDTO();
    	obj.setErrorURL("");
    	obj.setFailedURL("");
    	obj.setSuccessURL("");
    	obj.setIznos(new Float(10));
    	obj.setMerchantID("1");
    	obj.setMerchantPass("pass1");
    	obj.setMerchantTimestamp(new Date());
    	obj.setMerchantOrderID(new Long(11));
    	
        uplatilac = new Klijent("test", "testic", "1", "pass1", "test-mejl", new ArrayList<>());
    	uplatilac = klijentRepository.save(uplatilac);
    	karticaU = new Kartica(BNumber + "5555522225", "555", "1/25", BNumber + "005", 1000F, 0F, uplatilac );
    	karticaU = karticaRepository.save(karticaU);
    	uplatilac.getKartice().add(karticaU);
    	klijentRepository.save(uplatilac);
    	
    	primalac = new Klijent("test1", "testic1", "1", "pass1", "test-mejl1", new ArrayList<>());
    	primalac = klijentRepository.save(primalac);
    	karticaP = new Kartica(BNumber + "5555533335", "333", "1/25", BNumber + "006", 1000F, 0F, primalac );
    	karticaP = karticaRepository.save(karticaP);
    	primalac.getKartice().add(karticaP);
    	klijentRepository.save(primalac);
    	
        transakcija = new Transakcija(uplatilac, primalac, "3", new Date(System.currentTimeMillis()), Status.K,
        		karticaP.getPan(), null, 100F, "succ", "fail", "error", 5L, new Date(System.currentTimeMillis()));
        transakcija = transakcijaRepository.save(transakcija);
        paymentInfo = new PaymentInfo(transakcija,"3");
        paymentInfo = paymentInfoRepository.save(paymentInfo);
    }
    
    @Test
    public void a_testAll_thenStatus200()
      throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper(); 
    	
    	mvc.perform(get("/getTransactions")
    	         .contentType(MediaType.APPLICATION_JSON))
    	          .andExpect(status().isOk());
    	
        String json = objectMapper.writeValueAsString(obj);
        
        /*   mvc.perform(post("/initiatePayment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());*/

        
        
        mvc.perform(get("/pay/"+paymentInfo.getPaymentURL())
                .contentType(MediaType.APPLICATION_JSON)
                .param("token", paymentInfo.getPaymentURL()))
                .andExpect(status().isOk());
        
        BuyerInfoDTO buyerInfo = new BuyerInfoDTO();
        buyerInfo.setCvv(uplatilac.getKartice().get(0).getCcv());
        buyerInfo.setGodina(25);
        buyerInfo.setMesec(1);
        buyerInfo.setIme(uplatilac.getIme());
        buyerInfo.setPrezime(uplatilac.getPrezime());
        buyerInfo.setPan(uplatilac.getKartice().get(0).getPan());
        
        String json1 = objectMapper.writeValueAsString(buyerInfo);
        mvc.perform(post("/pay/"+paymentInfo.getPaymentURL())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json1)
                .param("token", paymentInfo.getPaymentURL()))
                .andExpect(status().isOk());
        
    }

}
