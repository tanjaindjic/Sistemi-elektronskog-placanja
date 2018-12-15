package sep.tim18.banka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import sep.tim18.banka.model.Klijent;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.PCCReplyDTO;
import sep.tim18.banka.model.dto.PCCRequestDTO;

public interface IssuerService {

    Transakcija createTransakcija(PCCRequestDTO request, Klijent klijent);
    void tryPayment(PCCRequestDTO request, Transakcija t, Klijent k) throws JsonProcessingException;
    void sendReply(PCCReplyDTO reply);
}
