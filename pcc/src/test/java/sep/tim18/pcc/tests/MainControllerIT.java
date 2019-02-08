package sep.tim18.pcc.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class MainControllerIT {
	@Autowired
    private MockMvc mvc; 
	
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
    
	@Test
    public void a_request_thenStatus200()
      throws Exception {
		PCCRequestDTO req = new PCCRequestDTO();
		req.setAcquirerOrderID(new Long(1));
		req.setAcquirerTimestamp(new Date());
		req.setBrojBankeProdavca(B1N);
		req.setCvv("555");
		req.setGodina(25);
		req.setIme("test");
		req.setIznos(new Float(10));
		req.setMerchantOrderID(new Long(1));
		req.setMerchantTimestamp(new Date());
		req.setMesec(1);
		req.setPanPosaljioca(B1N+"5555522225");
		req.setPanPrimaoca(B2N+"5555533335");
		req.setPrezime("trecic");
		req.setReturnURL("");
		
		
		
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(req);
        mvc.perform(post("/request")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());
    }
	@Test
    public void b_reply_thenStatus200()
      throws Exception {
		PCCReplyDTO rep = new PCCReplyDTO();
		rep.setAcquirerOrderID(new Long(1));
		rep.setIssuerOrderID(new Long(1));
		rep.setIssuerTimestamp(new Date());
		rep.setMerchantOrderID(new Long(1));
		rep.setStatus(Status.C);
		
    	ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(rep);
        mvc.perform(post("/reply")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
          .andExpect(status().isOk());
    }
	@Test
    public void c_getTransactions_thenStatus200()
      throws Exception {
        mvc.perform(get("/getTransactions")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
}
