package sep.tim18.pcc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;

import javax.validation.Valid;
import java.util.List;

public interface MainService {
    Banka getBankaByPan(String pan);
    Banka getBanka(String brBanke);
    void forward(Zahtev zahtev, PCCRequestDTO pcCrequestDTO, String url) throws JsonProcessingException;
    Zahtev createZahtev(PCCRequestDTO request);
    void sendReply(PCCReplyDTO pccReplyDTO, Zahtev returnURL);
    void finish(PCCReplyDTO replyDTO);
    Zahtev checkRequest(@Valid PCCRequestDTO request);
}
