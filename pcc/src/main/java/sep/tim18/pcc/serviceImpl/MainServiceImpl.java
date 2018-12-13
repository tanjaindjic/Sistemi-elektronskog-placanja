package sep.tim18.pcc.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sep.tim18.pcc.model.Banka;
import sep.tim18.pcc.model.Zahtev;
import sep.tim18.pcc.model.dto.PCCReplyDTO;
import sep.tim18.pcc.model.dto.PCCRequestDTO;
import sep.tim18.pcc.model.enums.Status;
import sep.tim18.pcc.repository.BankaRepository;
import sep.tim18.pcc.repository.ZahtevRepository;
import sep.tim18.pcc.service.MainService;

import java.net.URI;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private BankaRepository bankaRepository;


    @Autowired
    private ZahtevRepository zahtevRepository;

    @Override
    public Banka getBankaByPan(String pan) {
        String brojBanke = pan.substring(0,6);
        return bankaRepository.findByBrojBanke(brojBanke);

    }

    @Override
    public Banka getBanka(String brBanke) {
        return bankaRepository.findByBrojBanke(brBanke);
    }


    @Override
    public Mono<ResponseEntity> forward(Zahtev zahtev, PCCRequestDTO pccRequestDTO, String url) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(pccRequestDTO);

        WebClient client = WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        WebClient.RequestHeadersSpec<?> requestSpec = WebClient
                .create()
                .post()
                .uri(URI.create(url))
                .body(BodyInserters.fromObject(jsonInString));

        Mono<ClientResponse> clientResponse = requestSpec
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .exchange();

        clientResponse.subscribe((response)->{
            ClientResponse.Headers headers = response.headers();
            HttpStatus statusCode = response.statusCode();
            if (statusCode == HttpStatus.OK){
                //ako je banka kupca uspesno obradila transakciju
                Mono<PCCReplyDTO> bodyToMono = response.bodyToMono(PCCReplyDTO.class);
                // the second subscribe to access the body
                bodyToMono.subscribe((body) -> {

                    System.out.println("body:" + body);
                    System.out.println("headers:" + headers.asHttpHeaders());
                    System.out.println("stausCode:" + statusCode);
                    zahtev.setIssuerOrderID(body.getIssuerOrderID());

                }, (ex) -> {
                    // handle error
                });

                zahtev.setStatus(Status.U);
                zahtevRepository.save(zahtev);
            }else{
                zahtev.setStatus(Status.N);
                zahtevRepository.save(zahtev);
            }

        });

        if(zahtev.getStatus()==Status.U)
            return Mono.just(new ResponseEntity(HttpStatus.OK));
        else return Mono.just(new ResponseEntity(HttpStatus.BAD_REQUEST)); //banka kupca nije obradial transakciju

    }

    @Override
    public Zahtev createZahtev(PCCRequestDTO request) {
        Banka prodavca = bankaRepository.findByBrojBanke(request.getBrojBankeProdavca());
        Banka kupca = bankaRepository.findByBrojBanke(request.getPan().substring(0,6));
        Zahtev zahtev = new Zahtev();
        zahtev.setStatus(Status.P);
        zahtev.setAcquirerOrderID(request.getAcquirerOrderID());
        zahtev.setBankaKupca(kupca);
        zahtev.setBankaProdavca(prodavca);
        zahtev.setVremeKreiranja(new DateTime());

        return zahtev;
    }
}
