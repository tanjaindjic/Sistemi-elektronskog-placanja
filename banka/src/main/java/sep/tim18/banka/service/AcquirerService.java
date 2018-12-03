package sep.tim18.banka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.BuyerInfoDTO;
import sep.tim18.banka.model.dto.KPRequestDTO;

import java.util.Map;

public interface AcquirerService {

    boolean validate(KPRequestDTO request);
    String getToken();
    Transakcija createTransaction(KPRequestDTO request);
    boolean isTokenExpired(String token);
    ResponseEntity<Map> tryPayment(String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    ResponseEntity<Map> sendToPCC(Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    ResponseEntity<Map> paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    ResponseEntity<Map> finishPayment(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    PaymentInfo createPaymentDetails(KPRequestDTO request);
}
