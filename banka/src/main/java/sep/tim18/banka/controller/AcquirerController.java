package sep.tim18.banka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sep.tim18.banka.exceptions.FundsException;
import sep.tim18.banka.exceptions.NotFoundException;
import sep.tim18.banka.exceptions.PaymentException;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.*;
import sep.tim18.banka.model.enums.Status;
import sep.tim18.banka.service.AcquirerService;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.*;

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
    public ResponseEntity<Map> initiatePayment(@Valid @RequestBody KPRequestDTO request){
        System.out.println(request.toString());

        Map retVal = new HashMap<String, String>();

        if(!acquirerService.validate(request)){

            retVal.put("poruka", "MerchantID ili MerchantPass su neispravni.");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);

        }

        PaymentInfo paymentInfo = acquirerService.createPaymentDetails(request);
        retVal.put("paymentURL", BAddress + "#!/pay/" + paymentInfo.getPaymentURL());
        retVal.put("paymentID", paymentInfo.getPaymentID());
        retVal.put("izvrsnaTransakcijaId", paymentInfo.getTransakcija().getOrderID());

        return new ResponseEntity<Map>(retVal, HttpStatus.OK);
    }

    @RequestMapping(value = "/getTransactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> getTransactions() throws IOException {
        List<FinishedPaymentDTO> transakcije = new ArrayList<>();
        for(Transakcija t : acquirerService.getAllTransakcije())
            if(t.getStatus().equals(Status.K_KP) || t.getStatus().equals(Status.C) || t.getStatus().equals(Status.C_PCC) || t.getStatus().equals(Status.U_KP) || t.getStatus().equals(Status.N_KP) || t.getStatus().equals(Status.E_KP))
                transakcije.add(acquirerService.createFinishedPaymentDTO(t));

        return new ResponseEntity<>(transakcije, HttpStatus.OK);
    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.GET)
    public ResponseEntity<Map> getPaymentForm(HttpServletResponse httpServletResponse, @PathVariable String token) throws IOException {

        Map retVal = new HashMap<String, String>();

        if(acquirerService.findByPaymentURL(token) == null) {
            System.out.println("Nema informacija o ovom tokenu.");
            retVal.put("Location", "/404");
            return new ResponseEntity<>(retVal, HttpStatus.BAD_REQUEST);
        }

        if(acquirerService.isPaymentFinished(token)) {
            System.out.println("Transakcija je zavrsena.");
            retVal.put("Location", "/404");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);
        }

        if(acquirerService.isTransakcijaPending(token)){
            retVal.put("Location", "/paymentSent");
            return new ResponseEntity<>(retVal, HttpStatus.BAD_REQUEST);
        }

        if(acquirerService.isTokenExpired(token)) {
            System.out.println("Token " + token + " je istekao.");
            retVal.put("Location", "/expired");
            return new ResponseEntity<Map>(retVal, HttpStatus.BAD_REQUEST);
        }
        else{
            System.out.println("Token " + token + " nije istekao.");
            retVal.put("Location", "pay/" + token);
            return new ResponseEntity<Map>(retVal, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/pay/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> postPaymentForm(HttpServletResponse httpServletResponse, @PathVariable String token, @Valid @RequestBody BuyerInfoDTO buyerInfoDTO) throws IOException, PaymentException, NotFoundException, FundsException, ParseException {

        Map<String, String> map = new HashMap<>();

        PaymentInfo paymentInfo = acquirerService.findByPaymentURL(token);
        if(paymentInfo == null){
            System.out.println("Token ne postoji.");
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        Transakcija t = paymentInfo.getTransakcija();
        if(t == null){
            System.out.println("Transakcija ne postoji.");
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if(acquirerService.isTransakcijaPending(token) || acquirerService.isTokenExpired(token)){
            System.out.println("Transakcija je vec zapoceta.");
            map.put("Location", "/failed");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if(acquirerService.checkCredentials(token, buyerInfoDTO)){
            System.out.println("Podaci su validni.");

            try {
                return acquirerService.tryPayment(token, buyerInfoDTO, httpServletResponse);

            }catch (PaymentException e){
                System.out.println(e.getMessage());
                acquirerService.paymentFailed(paymentInfo, t, token, buyerInfoDTO, false);
                map.put("Location", "/failed");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

            }catch (NotFoundException e){
                System.out.println(e.getMessage());
                map.put("Poruka", "Podaci nisu ispravni.");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

            }catch (FundsException e){
                System.out.println("Nedovoljno sredstava.");
                acquirerService.paymentFailed(paymentInfo, t, token, buyerInfoDTO, true);
                map.put("Location", "/failed");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        }
        else{
            System.out.println("Podaci nisu validni.");
            map.put("Poruka", "Podaci nisu ispravni."); //samo vratim odgovor da je omasio br kartice
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/pccReply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void pccReply(@Valid @RequestBody PCCReplyDTO pccReplyDTO) throws NotFoundException {

        System.out.println("Odgovor od PCC-a: " + pccReplyDTO.toString());
        try {
            acquirerService.finalizePayment(pccReplyDTO);
        }catch (NotFoundException e){
            System.out.println(e.getMessage());;
        }
    }



}
