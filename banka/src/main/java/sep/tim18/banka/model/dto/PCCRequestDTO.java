package sep.tim18.banka.model.dto;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PCCRequestDTO {
    @NotNull
    private Long acquirerOrderID;
    @NotNull
    private Date acquirerTimestamp;
    @NotNull
    private Long merchantOrderID;
    @NotNull
    private Date merchantTimestamp;

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

    public PCCRequestDTO() {
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

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    public Float getIznos() {
        return iznos;
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

    @Override
    public String toString() {
        return "PCCRequestDTO{" +
                "acquirerOrderID=" + acquirerOrderID +
                ", acquirerTimestamp=" + acquirerTimestamp +
                ", merchantOrderID=" + merchantOrderID +
                ", merchantTimestamp=" + merchantTimestamp +
                ", panPosaljioca='" + panPosaljioca + '\'' +
                ", cvv='" + cvv + '\'' +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", mesec=" + mesec +
                ", godina=" + godina +
                ", returnURL='" + returnURL + '\'' +
                ", iznos=" + iznos +
                ", panPrimaoca='" + panPrimaoca + '\'' +
                ", brojBankeProdavca='" + brojBankeProdavca + '\'' +
                '}';
    }
}
