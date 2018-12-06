package sep.tim18.pcc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCRequestDTO;

public interface MainService {
    Banka getBanka(String pan);
    ResponseEntity forward(Zahtev zahtev, PCCRequestDTO pcCrequestDTO, String url) throws JsonProcessingException;
    Zahtev createZahtev(PCCRequestDTO request);
}
