package sep.tim18.banka.service;

import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.dto.RequestDTO;

public interface MainService {

    boolean validate(RequestDTO request);
    String getToken();
    Transakcija createTransaction(RequestDTO request);
    boolean isTokenExpired(String token);
}
