package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.dto.BuyerInfoDTO;
import sep.tim18.banka.model.dto.KPRequestDTO;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.service.AcquirerService;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AcquirerController {

    private static String BAddress;

    @Value("${BAddress}")
    public void setB1URL(String bankAdress) {
        BAddress = bankAdress;
    }

    @Autowired
    private AcquirerService acquirerService;

    @RequestMapping(value = "/initiatePayment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> request(@RequestBody KPRequestDTO request){

        Map retVal = new HashMap<String, String>();

        if(!acquirerService.validate(request)){
            retVal.put("poruka", "MerchantID ili MerchantPass su neispravni.");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);

        }

        PaymentInfo paymentInfo = acquirerService.createPaymentDetails(request);

        retVal.put("paymentURL", BAddress + "pay/" +paymentInfo.getPaymentURL());
        retVal.put("paymentID", paymentInfo.getPaymentID());

        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.GET)
    public ResponseEntity<Map>  method(HttpServletResponse httpServletResponse, @PathVariable String token) throws IOException {
        System.out.println("USAO U GET PAY");
        Map retVal = new HashMap<String, String>();
        if(acquirerService.isTokenExpired(token)) {
            System.out.println("token " + token + " je istekao");
            retVal.put("Location", "/expired");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);
        }
        else{
            System.out.println("token NIJE istekao");
            retVal.put("Location", "pay/" + token);
            return new ResponseEntity<Map>(retVal, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> finishPayment(HttpServletResponse httpServletResponse, @PathVariable String token, @RequestBody BuyerInfoDTO buyerInfoDTO) throws IOException {

        return acquirerService.tryPayment(token, buyerInfoDTO, httpServletResponse);

    }
  /*  @RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void salji(){
        PCCReplyDTO novi = new PCCReplyDTO();
        novi.setIssuerOrderID(226883L);
        novi.setIssuerTimestamp(new Date(System.currentTimeMillis()));
        RestTemplate t = new RestTemplate();

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        ResponseEntity<PCCReplyDTO> response = t.postForEntity("https://localhost:8086/test3", novi, PCCReplyDTO.class);
        System.out.println("test: " + response.getBody());
    }


    @RequestMapping(value = "/test1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void primi(@RequestBody PCCReplyDTO pccReplyDTO){
        System.out.println("test1 primio: " + pccReplyDTO.toString());
    }

    @RequestMapping(value = "/test3", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PCCReplyDTO> salji(@RequestBody PCCReplyDTO pccReplyDTO){
        System.out.println("test 3 primio 1: " + pccReplyDTO.toString());

        PCCReplyDTO novi = new PCCReplyDTO();
        novi.setIssuerOrderID(515L);
        novi.setIssuerTimestamp(new Date(System.currentTimeMillis()));
        RestTemplate t = new RestTemplate();
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session)->true);
        ResponseEntity<PCCReplyDTO> response = t.postForEntity("https://localhost:8082/test1", novi, PCCReplyDTO.class);

        return new ResponseEntity<>(novi, HttpStatus.OK);
    }
*/

}
