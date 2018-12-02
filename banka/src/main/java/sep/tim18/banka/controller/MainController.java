package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(value = "/startPayment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> request(@RequestBody RequestDTO request){

        Map retVal = new HashMap<String, String>();

        if(!mainService.validate(request))
            return new ResponseEntity<Map>(retVal, HttpStatus.I_AM_A_TEAPOT);//https://www.google.com/teapot :D

        Transakcija t = mainService.createTransaction(request);
        retVal.put("paymentURL", t.getPaymentURL());
        retVal.put("paymentID", t.getId());

        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse, @PathVariable String token) throws IOException {

        httpServletResponse.sendRedirect(siteAddress + "/pay/" + token);

        //TODO promeniti na https posle
        if(mainService.isTokenExpired(token)){
            httpServletResponse.sendRedirect(siteAddress + "/expired");
        }else httpServletResponse.sendRedirect(siteAddress + "/pay/" + token);

    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.POST)
    public ResponseEntity<Map> finishPayment(HttpServletResponse httpServletResponse, @PathVariable String token, @RequestBody PaymentDTO paymentDTO) throws IOException {
        if(mainService.isTokenExpired(token))
            httpServletResponse.sendRedirect(siteAddress + "/expired");
        return mainService.tryPayment(token, paymentDTO);

    }

}
