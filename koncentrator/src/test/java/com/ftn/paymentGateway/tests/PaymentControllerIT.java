package com.ftn.paymentGateway.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
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
public class PaymentControllerIT {
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
    
    
    @Test
    public void a_whenSendPaymentRequest_thenStatus200()
      throws Exception {
    	EntitetPlacanjaDTO epDTO = new EntitetPlacanjaDTO("AAA", null);
    	EntitetPlacanja ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, null);
    	ep = entitetPlacanjaRepository.save(ep);
    	PaymentRequestDTO randomObj = new PaymentRequestDTO(epDTO, new Double(10), false, new Long(1), "","", "");
        
    	
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(randomObj);
        mvc.perform(post("/rest/sendPaymentRequest")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());
    }
    @Test
    public void b_whenDoPayment_thenStatus200()
      throws Exception {
    	String token = randomStringGenerator.genRandomString(90);
    	TipPlacanja tp = tipPlacanjaRepository.findByKod("CCP");
    	Long id = tp.getId();    	
    	EntitetPlacanja ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, null);
    	ep = entitetPlacanjaRepository.save(ep);
    	PoljePodrzanoPlacanje ppb = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, RSAEncryptDecrypt.encrypt("1"));
    	PoljePodrzanoPlacanje ppb1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, RSAEncryptDecrypt.encrypt("pass1"));
		
    	ppb = poljePodrzanoPlacanjeRepository.save(ppb);
    	ppb1 = poljePodrzanoPlacanjeRepository.save(ppb1);
    	List<PoljePodrzanoPlacanje> banka1PP = new ArrayList<>();
		banka1PP.add(ppb);
		banka1PP.add(ppb1);
		PodrzanoPlacanje pp1 = new PodrzanoPlacanje(null, false, banka1PP, ep, tp);
		pp1 = podrzanoPlacanjeRepository.save(pp1);
		
    	Transakcija tr = new Transakcija(null, new Long(10), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token, false, ep, tp, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
        transakcijaRepository.save(tr);
        
    	mvc.perform(post("/rest/doPayment")
          .contentType(MediaType.APPLICATION_JSON)
          .param("paymentTypeId", id.toString())
      	  .param("uniqueToken", token))
          .andExpect(status().isOk());
    }
    @Test
    public void c_whenCompletePayment_thenStatus200()
      throws Exception {
    	String token = randomStringGenerator.genRandomString(90);
    	TipPlacanja tp = tipPlacanjaRepository.findByKod("CCP");
    	Long id = tp.getId();    	
    	EntitetPlacanja ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, null);
    	ep = entitetPlacanjaRepository.save(ep);
    	PoljePodrzanoPlacanje ppb = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_ID, RSAEncryptDecrypt.encrypt("1"));
    	PoljePodrzanoPlacanje ppb1 = new PoljePodrzanoPlacanje(null, IdPoljePlacanja.MERCHANT_PASSWORD, RSAEncryptDecrypt.encrypt("pass1"));
		
    	ppb = poljePodrzanoPlacanjeRepository.save(ppb);
    	ppb1 = poljePodrzanoPlacanjeRepository.save(ppb1);
    	List<PoljePodrzanoPlacanje> banka1PP = new ArrayList<>();
		banka1PP.add(ppb);
		banka1PP.add(ppb1);
		PodrzanoPlacanje pp1 = new PodrzanoPlacanje(null, false, banka1PP, ep, tp);
		pp1 = podrzanoPlacanjeRepository.save(pp1);
		
    	Transakcija tr = new Transakcija(null, new Long(10), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token, false, ep, tp, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
        transakcijaRepository.save(tr);
    	BankResponseDTO bankResp = new BankResponseDTO(tr.getId(), new Long(2), new Date(), new Long(3), TransakcijaStatus.U, "");
       
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(bankResp);
        
    	mvc.perform(post("/rest/bankResponse")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());
    }
    
    @Test
    public void d_whenProveriStatusTransakcije_thenStatus200()
      throws Exception {
    	String token = randomStringGenerator.genRandomString(90);
    	TipPlacanja tp = tipPlacanjaRepository.findByKod("CCP");
    	Long id = tp.getId();    	
    	EntitetPlacanja ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, null);
    	ep = entitetPlacanjaRepository.save(ep);
    	
		
    	Transakcija tr = new Transakcija(null, new Long(10), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token, false, ep, tp, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
        transakcijaRepository.save(tr);
    	
    	mvc.perform(get("/rest/proveriStatusTransakcije")
          .contentType(MediaType.APPLICATION_JSON)
          .param("uniqueToken", token))
          .andExpect(status().isOk());
    }
    @Test
    public void e_whenObaviVracanje_thenStatus200()
      throws Exception {
    	String token = randomStringGenerator.genRandomString(90);
    	TipPlacanja tp = tipPlacanjaRepository.findByKod("CCP");
    	Long id = tp.getId();    	
    	EntitetPlacanja ep = new EntitetPlacanja(null, "Casopis TEST", "CAST#CAST#", false, null, null);
    	ep = entitetPlacanjaRepository.save(ep);
    	
		
    	Transakcija tr = new Transakcija(null, new Long(10), null, 5.00, new Date(System.currentTimeMillis()), TransakcijaStatus.C, token, false, ep, tp, "https://localhost:8098/paymentGateway/#!/success", "/failed", "/error");
        transakcijaRepository.save(tr);
    	
    	mvc.perform(get("/rest/obaviVracanje")
          .contentType(MediaType.APPLICATION_JSON)
          .param("uniqueToken", token))
          .andExpect(status().isOk());
    }
}
