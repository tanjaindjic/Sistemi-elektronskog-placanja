package sep.tim18.banka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.BuyerInfoDTO;
import sep.tim18.banka.model.dto.KPRequestDTO;
import sep.tim18.banka.model.dto.PCCReplyDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface AcquirerService {

    boolean validate(KPRequestDTO request);
    String getToken();
    Transakcija createTransaction(KPRequestDTO request);
    boolean isTokenExpired(String token);
    ResponseEntity<Map> tryPayment(String token, BuyerInfoDTO buyerInfoDTO, HttpServletResponse resp) throws JsonProcessingException, IOException;
    void sendToPCC(Transakcija t, String token, BuyerInfoDTO buyerInfoDTO, PaymentInfo paymentInfo, HttpServletResponse resp) throws JsonProcessingException;
    void paymentFailed(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    void finishPayment(PaymentInfo paymentInfo, Transakcija t, String token, BuyerInfoDTO buyerInfoDTO) throws JsonProcessingException;
    PaymentInfo createPaymentDetails(KPRequestDTO request);
    void finalizePayment(PCCReplyDTO pccReplyDTO);
    boolean finishedPayment(String token);
    boolean checkCredentials(String token, BuyerInfoDTO buyerInfoDTO);
}
