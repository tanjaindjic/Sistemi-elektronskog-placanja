package sep.tim18.pcc.model;

import org.hibernate.validator.constraints.Length;
import sep.tim18.pcc.model.dto.PCCRequestDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class PCCRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private Long acquirerOrderID;
    @NotNull
    private Date acquirerTimestamp;
    @NotNull
    private Long merchantOrderID;
    @NotNull
    private Date merchantTimestamp;
    @NotNull
    @Length(min = 8, max = 19)
    private String panPosaljioca;
    @NotNull
    @Length(min = 3, max = 4)
    private String cvv;
    @NotNull
    @Length(min = 1)
    private String ime;
    @NotNull
    @Length(min = 1)
    private String prezime;
    @NotNull
    private int mesec;
    @NotNull
    private int godina;

    private String returnURL;
    @NotNull
    private Float iznos;

    private String panPrimaoca;

    private String brojBankeProdavca;

    public PCCRequest() {
    }

    public PCCRequest(PCCRequestDTO pccRequestDTO) {
        this.acquirerOrderID =  pccRequestDTO.getAcquirerOrderID();
        this.acquirerTimestamp =  pccRequestDTO.getAcquirerTimestamp();
        this.merchantOrderID =  pccRequestDTO.getMerchantOrderID();
        this.merchantTimestamp =  pccRequestDTO.getMerchantTimestamp();
        this.panPosaljioca =  pccRequestDTO.getPanPosaljioca();
        this.cvv =  pccRequestDTO.getCvv();
        this.ime =  pccRequestDTO.getIme();
        this.prezime =  pccRequestDTO.getPrezime();
        this.mesec =  pccRequestDTO.getMesec();
        this.godina =  pccRequestDTO.getGodina();
        this.returnURL =  pccRequestDTO.getReturnURL();
        this.iznos =  pccRequestDTO.getIznos();
        this.panPrimaoca =  pccRequestDTO.getPanPrimaoca();
        this.brojBankeProdavca =  pccRequestDTO.getBrojBankeProdavca();
    }

    public long getId() {
        return id;
    }

    public Long getAcquirerOrderID() {
        return acquirerOrderID;
    }

    public void setAcquirerOrderID(Long acquirerOrderID) {
        this.acquirerOrderID = acquirerOrderID;
    }

    public Date getAcquirerTimestamp() {
        return acquirerTimestamp;
    }

    public void setAcquirerTimestamp(Date acquirerTimestamp) {
        this.acquirerTimestamp = acquirerTimestamp;
    }

    public Long getMerchantOrderID() {
        return merchantOrderID;
    }

    public void setMerchantOrderID(Long merchantOrderID) {
        this.merchantOrderID = merchantOrderID;
    }

    public Date getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(Date merchantTimestamp) {
        this.merchantTimestamp = merchantTimestamp;
    }

    public String getPanPosaljioca() {
        return panPosaljioca;
    }

    public void setPanPosaljioca(String panPosaljioca) {
        this.panPosaljioca = panPosaljioca;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public int getMesec() {
        return mesec;
    }

    public void setMesec(int mesec) {
        this.mesec = mesec;
    }

    public int getGodina() {
        return godina;
    }

    public void setGodina(int godina) {
        this.godina = godina;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public Float getIznos() {
        return iznos;
    }

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    public String getPanPrimaoca() {
        return panPrimaoca;
    }

    public void setPanPrimaoca(String panPrimaoca) {
        this.panPrimaoca = panPrimaoca;
    }

    public String getBrojBankeProdavca() {
        return brojBankeProdavca;
    }

    public void setBrojBankeProdavca(String brojBankeProdavca) {
        this.brojBankeProdavca = brojBankeProdavca;
    }
}
