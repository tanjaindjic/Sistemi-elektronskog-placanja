package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.dto.BuyerInfoDTO;
import sep.tim18.banka.model.dto.KPRequestDTO;
import sep.tim18.banka.service.AcquirerService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public void method(HttpServletResponse httpServletResponse, @PathVariable String token) throws IOException {

        //TODO promeniti na https posle
        if(acquirerService.isTokenExpired(token)){
            httpServletResponse.sendRedirect(BAddress + "/expired");
        }else httpServletResponse.sendRedirect(BAddress + "/pay/" + token);

    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.POST)
    public ResponseEntity<Map> finishPayment(HttpServletResponse httpServletResponse, @PathVariable String token, @RequestBody BuyerInfoDTO buyerInfoDTO) throws IOException {

        return acquirerService.tryPayment(token, buyerInfoDTO);

    }

    @RequestMapping(value = "/pccReply", method = RequestMethod.POST)
    public ResponseEntity pccReply(){
        return null;
    }




}
