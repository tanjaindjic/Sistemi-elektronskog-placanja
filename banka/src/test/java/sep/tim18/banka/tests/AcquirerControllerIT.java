package sep.tim18.banka.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;

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
public class AcquirerControllerIT {
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
	 
    @Test
    public void a_initiatePayment_thenStatus200()
      throws Exception {
    	KPRequestDTO obj = new KPRequestDTO();
    	obj.setErrorURL("");
    	obj.setFailedURL("");
    	obj.setSuccessURL("");
    	obj.setIznos(new Float(10));
    	obj.setMerchantID("1");
    	obj.setMerchantPass("pass1");
    	obj.setMerchantTimestamp(new Date());
    	obj.setMerchantOrderID(new Long(11));
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(obj);
        mvc.perform(post("/initiatePayment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());
    }
    
    @Test
    public void b_getTransactions_thenStatus200()
      throws Exception {

        mvc.perform(get("/getTransactions")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
    @Test
    public void c_getPaymentForm_thenStatus200()
      throws Exception {
    	Klijent klijent = new Klijent("test", "testic", "1", "pass1", "test-mejl", new ArrayList<>());
    	klijent = klijentRepository.save(klijent);
    	Kartica kartica = new Kartica(BNumber + "5555522225", "555", "1/25", BNumber + "005", 0F, 0F, klijent );
    	kartica = karticaRepository.save(kartica);
        Transakcija transakcija = new Transakcija(null, klijent, "1", new Date(System.currentTimeMillis()), Status.K,
                 kartica.getPan(), null, 100F, "succ", "fail", "error", 5L, new Date(System.currentTimeMillis()));
        transakcija = transakcijaRepository.save(transakcija);
        PaymentInfo paymentInfo = new PaymentInfo(transakcija, "5");
        paymentInfo = paymentInfoRepository.save(paymentInfo);
        mvc.perform(get("/pay/"+paymentInfo.getPaymentURL())
          .contentType(MediaType.APPLICATION_JSON)
          .param("token", paymentInfo.getPaymentURL()))
          .andExpect(status().isOk());
    }
    @Test
    public void d_postPaymentForm_thenStatus200()
      throws Exception {
    	Klijent uplatilac = new Klijent("test", "testic", "1", "pass1", "test-mejl", new ArrayList<>());
    	uplatilac = klijentRepository.save(uplatilac);
    	Kartica karticaU = new Kartica(BNumber + "5555522225", "555", "1/25", BNumber + "005", 1000F, 0F, uplatilac );
    	karticaU = karticaRepository.save(karticaU);
    	uplatilac.getKartice().add(karticaU);
    	klijentRepository.save(uplatilac);
    	
    	Klijent primalac = new Klijent("test1", "testic1", "1", "pass1", "test-mejl1", new ArrayList<>());
    	primalac = klijentRepository.save(primalac);
    	Kartica karticaP = new Kartica(BNumber + "5555533335", "333", "1/25", BNumber + "006", 1000F, 0F, primalac );
    	karticaP = karticaRepository.save(karticaP);
    	primalac.getKartice().add(karticaP);
    	klijentRepository.save(primalac);
    	
        Transakcija transakcija = new Transakcija(uplatilac, primalac, "3", new Date(System.currentTimeMillis()), Status.K,
        		karticaP.getPan(), null, 100F, "succ", "fail", "error", 5L, new Date(System.currentTimeMillis()));
        transakcija = transakcijaRepository.save(transakcija);
        PaymentInfo paymentInfo = new PaymentInfo(transakcija,"3");
        paymentInfo = paymentInfoRepository.save(paymentInfo);
        
        BuyerInfoDTO buyerInfo = new BuyerInfoDTO();
        buyerInfo.setCvv(uplatilac.getKartice().get(0).getCcv());
        buyerInfo.setGodina(25);
        buyerInfo.setMesec(1);
        buyerInfo.setIme(uplatilac.getIme());
        buyerInfo.setPrezime(uplatilac.getPrezime());
        buyerInfo.setPan(uplatilac.getKartice().get(0).getPan());
        
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(buyerInfo);
        
        mvc.perform(post("/pay/"+paymentInfo.getPaymentURL())
          .contentType(MediaType.APPLICATION_JSON)
          .content(json)
          .param("token", paymentInfo.getPaymentURL()))
          .andExpect(status().isOk());
    }
}
