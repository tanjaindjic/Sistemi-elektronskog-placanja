package com.ftn.paymentGateway.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftn.paymentGateway.dto.BankResponseDTO;
import com.ftn.paymentGateway.dto.EntitetPlacanjaDTO;
import com.ftn.paymentGateway.dto.PaymentRequestDTO;
import com.ftn.paymentGateway.enumerations.IdPoljePlacanja;
import com.ftn.paymentGateway.enumerations.TransakcijaStatus;
import com.ftn.paymentGateway.helpClasses.RSAEncryptDecrypt;
import com.ftn.paymentGateway.helpClasses.RandomStringGenerator;
import com.ftn.paymentGateway.model.EntitetPlacanja;
import com.ftn.paymentGateway.model.PodrzanoPlacanje;
import com.ftn.paymentGateway.model.PoljePodrzanoPlacanje;
import com.ftn.paymentGateway.model.TipPlacanja;
import com.ftn.paymentGateway.model.Transakcija;
import com.ftn.paymentGateway.repository.EntitetPlacanjaRepository;
import com.ftn.paymentGateway.repository.PodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.PoljePodrzanoPlacanjeRepository;
import com.ftn.paymentGateway.repository.TipPlacanjaRepository;
import com.ftn.paymentGateway.repository.TransakcijaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTests {
	@Autowired
    private MockMvc mvc; 
    @Autowired
    private EntitetPlacanjaRepository entitetPlacanjaRepository;
    @Autowired
	private RandomStringGenerator randomStringGenerator;
    @Autowired
	private TipPlacanjaRepository tipPlacanjaRepository;
    @Autowired
	private TransakcijaRepository transakcijaRepository;
    @Autowired
	private PoljePodrzanoPlacanjeRepository poljePodrzanoPlacanjeRepository;
    @Autowired
	private PodrzanoPlacanjeRepository podrzanoPlacanjeRepository;
    
    private EntitetPlacanja ep;
    private PaymentRequestDTO randomObj;
    private EntitetPlacanjaDTO epDTO;
    private Transakcija tr;
    private String token;
    private  TipPlacanja tp;
    private PodrzanoPlacanje pp1 ;
    
    @PostConstruct
    public void setUp() {
    	ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, "");
    	ep = entitetPlacanjaRepository.save(ep);  	
    	epDTO = new EntitetPlacanjaDTO("AAA", null);
    	randomObj = new PaymentRequestDTO(epDTO, new Double(10), false, new Long(1), "","", "");
    	token = randomStringGenerator.genRandomString(90);
    	tp = tipPlacanjaRepository.findByKod("CCP");
    	tr = new Transakcija(null, new Long(10), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token, false, ep, tp, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error", false);
    	tr = transakcijaRepository.save(tr);
    	PoljePodrzanoPlacanje ppb = null;
    	PoljePodrzanoPlacanje ppb1 = null;
		try {
			ppb = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, RSAEncryptDecrypt.encrypt("1"));
	    	ppb1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, RSAEncryptDecrypt.encrypt("pass1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	ppb = poljePodrzanoPlacanjeRepository.save(ppb);
    	ppb1 = poljePodrzanoPlacanjeRepository.save(ppb1);
    	List<PoljePodrzanoPlacanje> banka1PP = new ArrayList<>();
		banka1PP.add(ppb);
		banka1PP.add(ppb1);
		pp1 = new PodrzanoPlacanje(null, false, banka1PP, ep, tp);
		pp1 = podrzanoPlacanjeRepository.save(pp1);
    }
    
    @Test
    public void b_whenDoPayment_thenStatus200()
      throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(randomObj);
    	Long id = tp.getId();    		
    	mvc.perform(post("/rest/sendPaymentRequest")
    	          .contentType(MediaType.APPLICATION_JSON)
    	          .content(json))
    	          .andExpect(status().isOk());
    	
    	mvc.perform(post("/rest/doPayment")
          .contentType(MediaType.APPLICATION_JSON)
          .param("paymentTypeId", id.toString())
      	  .param("uniqueToken", token))
          .andExpect(status().isOk());
    	
    	BankResponseDTO bankResp = new BankResponseDTO(tr.getId(), new Long(2), new Date(), new Long(3), TransakcijaStatus.U, "");
        String json2 = objectMapper.writeValueAsString(bankResp);
        
    	mvc.perform(post("/rest/bankResponse")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json2))
          .andExpect(status().isOk());
    	
    	mvc.perform(get("/rest/proveriStatusTransakcije")
    	          .contentType(MediaType.APPLICATION_JSON)
    	          .param("uniqueToken", token))
    	          .andExpect(status().isOk());
    	
    	mvc.perform(get("/rest/obaviVracanje")
    	          .contentType(MediaType.APPLICATION_JSON)
    	          .param("uniqueToken", token))
    	          .andExpect(status().isOk());
    }

}
