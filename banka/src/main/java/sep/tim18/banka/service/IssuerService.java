package sep.tim18.banka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import sep.tim18.banka.exceptions.PaymentException;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.model.dto.PCCRequestDTO;

import javax.validation.Valid;
import java.util.List;

public interface IssuerService {

    Transakcija createTransakcija(PCCRequestDTO request, Klijent klijent);
    void processPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException, PaymentException;
    void sendReply(PCCReplyDTO reply, Transakcija t);
    boolean checkCredentials(PCCRequestDTO request, Klijent k);
    void checkPayment(@Valid PCCRequestDTO request) throws JsonProcessingException, PaymentException;
    List<Transakcija> getAllTransakcije();
}
