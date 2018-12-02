package sep.tim18.banka.model;

import sep.tim18.banka.model.enums.Status;

import javax.persistence.*;

import org.joda.time.DateTime;

@Entity
public class Transakcija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Klijent uplacuje;

    @ManyToOne
    private Klijent prima;

    private String paymentURL;

    private DateTime vremeKreiranja;

    private DateTime vremeIzvrsenja;

    private Status status;

    private String racunPrimaoca;

    private String racunPosiljaoca;

    private Float iznos;

    private String successURL;

    private String failedURL;

    private String errorURL;

    private Long parentTransactionId;
    //ovo pise u specifikaciji da se salje sa KP
    private Long merchantOrderId; //id transakcije sa KP

    private DateTime merchantTimestamp; //timestamp KP transakcije

    public Transakcija() {
    }

    public Transakcija(Transakcija original) {
        this.uplacuje = original.uplacuje;
        this.prima = original.prima;
        this.paymentURL = original.paymentURL;
        this.vremeKreiranja = new DateTime();
        this.vremeIzvrsenja = null;
        this.status = original.status;
        this.racunPrimaoca = original.racunPrimaoca;
        this.racunPosiljaoca = original.racunPosiljaoca;
        this.iznos = original.iznos;
        this.successURL = original.successURL;
        this.failedURL = original.failedURL;
        this.errorURL = original.errorURL;
        this.merchantOrderId = original.merchantOrderId;
        this.merchantTimestamp = original.merchantTimestamp;
        this.parentTransactionId = original.id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DateTime getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(DateTime vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public DateTime getVremeIzvrsenja() {
        return vremeIzvrsenja;
    }

    public void setVremeIzvrsenja(DateTime vremeIzvrsenja) {
        this.vremeIzvrsenja = vremeIzvrsenja;
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

    public Long getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(Long parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
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

