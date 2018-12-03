package sep.tim18.banka.model;

import sep.tim18.banka.model.enums.Status;

import javax.persistence.*;

import org.joda.time.DateTime;

@Entity
public class Transakcija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;

    @ManyToOne
    private Klijent uplacuje;

    @ManyToOne
    private Klijent prima;

    private String paymentURL; //ne treba za issuer transakciju jer je ovo vezano samo za token

    private DateTime timestamp; //ako transakcija u banci prodavca onda Acquirer timestamp a ako je od kupca onda je issuer timestamp

    private Status status;

    private String racunPrimaoca;

    private String racunPosiljaoca;

    private Float iznos;

    private String successURL;

    private String failedURL;

    private String errorURL;
    //ovo pise u specifikaciji da se salje sa KP
    private Long merchantOrderId; //id transakcije sa NC

    private DateTime merchantTimestamp; //timestamp NC transakcije

    public Transakcija() {
    }

    public Transakcija(Transakcija original) {
        this.uplacuje = original.uplacuje;
        this.prima = original.prima;
        this.paymentURL = original.paymentURL;
        this.timestamp = new DateTime();
        this.status = original.status;
        this.racunPrimaoca = original.racunPrimaoca;
        this.racunPosiljaoca = original.racunPosiljaoca;
        this.iznos = original.iznos;
        this.successURL = original.successURL;
        this.failedURL = original.failedURL;
        this.errorURL = original.errorURL;
        this.merchantOrderId = original.merchantOrderId;
        this.merchantTimestamp = original.merchantTimestamp;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long id) {
        this.orderID = id;
    }

    public Klijent getUplacuje() {
        return uplacuje;
    }

    public void setUplacuje(Klijent uplacuje) {
        this.uplacuje = uplacuje;
    }

    public Klijent getPrima() {
        return prima;
    }

    public void setPrima(Klijent prima) {
        this.prima = prima;
    }


    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime vremeKreiranja) {
        this.timestamp = vremeKreiranja;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRacunPrimaoca() {
        return racunPrimaoca;
    }

    public void setRacunPrimaoca(String racunPrimaoca) {
        this.racunPrimaoca = racunPrimaoca;
    }

    public String getRacunPosiljaoca() {
        return racunPosiljaoca;
    }

    public void setRacunPosiljaoca(String racunPosiljaoca) {
        this.racunPosiljaoca = racunPosiljaoca;
    }

    public Float getIznos() {
        return iznos;
    }

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    public String getFailedURL() {
        return failedURL;
    }

    public void setFailedURL(String failedURL) {
        this.failedURL = failedURL;
    }

    public String getErrorURL() {
        return errorURL;
    }

    public void setErrorURL(String errorURL) {
        this.errorURL = errorURL;
    }

    public Long getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(Long merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public DateTime getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(DateTime merchantTimestamp) {
        this.merchantTimestamp = merchantTimestamp;
    }
}

