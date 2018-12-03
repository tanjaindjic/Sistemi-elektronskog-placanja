package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PaymentDTO;
import sep.tim18.banka.model.dto.RequestDTO;
import sep.tim18.banka.service.MainService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @Value("${siteAddress}")
    private String siteAddress;

    @Autowired
    private MainService mainService;

    @RequestMapping(value = "/initiatePayment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> request(@RequestBody RequestDTO request){

        Map retVal = new HashMap<String, String>();

        if(!mainService.validate(request)){
            retVal.put("poruka", "MerchantID ili MerchantPass su neispravni.");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);

        }

        PaymentInfo paymentInfo = mainService.createPaymentDetails(request);

        retVal.put("paymentURL", siteAddress + "pay/" +paymentInfo.getPaymentURL());
        retVal.put("paymentID", paymentInfo.getPaymentID());

        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse, @PathVariable String token) throws IOException {

        //TODO promeniti na https posle
        if(mainService.isTokenExpired(token)){
            httpServletResponse.sendRedirect(siteAddress + "/expired");
        }else httpServletResponse.sendRedirect(siteAddress + "/pay/" + token);

    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.POST)
    public ResponseEntity<Map> finishPayment(HttpServletResponse httpServletResponse, @PathVariable String token, @RequestBody PaymentDTO paymentDTO) throws IOException {
        if(mainService.isTokenExpired(token)){
            //TODO poslati failed na KP
            Map mapa = new HashMap();
            mapa.put("location", "/expired");
            return new ResponseEntity<>(mapa, HttpStatus.BAD_REQUEST);
        }

        return mainService.tryPayment(token, paymentDTO);

    }



}
