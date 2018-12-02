package sep.tim18.banka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PaymentDTO;
import sep.tim18.banka.model.dto.RequestDTO;

import java.util.Map;

public interface MainService {

    boolean validate(RequestDTO request);
    String getToken();
    Transakcija createTransaction(RequestDTO request);
    boolean isTokenExpired(String token);
    ResponseEntity<Map> tryPayment(String token, PaymentDTO paymentDTO) throws JsonProcessingException;
    ResponseEntity<Map> sendToPCC(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException;
    ResponseEntity<Map> paymentFailed(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException;
    ResponseEntity<Map> finishPayment(Transakcija t, String token, PaymentDTO paymentDTO) throws JsonProcessingException;
}
