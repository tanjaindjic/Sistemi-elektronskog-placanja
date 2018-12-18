package sep.tim18.banka.model;

import sep.tim18.banka.model.enums.Status;

import java.util.Date;

import javax.persistence.*;


@Entity
public class Transakcija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;


    @ManyToOne//(optional=false) ne moze jer kod banke prodavca ovo moze biti null ako nisu iz iste banke
    private Klijent uplacuje;

    @ManyToOne//(optional=false) ne moze jer kod banke kupca ovo moze biti null jer nisu iz iste banke
    private Klijent prima;

    @Column(nullable = false, length=256)
    private String paymentURL; //ne treba za issuer transakciju jer je ovo vezano samo za token

    @Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
    private Date timestamp; //ako transakcija u banci prodavca onda Acquirer timestamp a ako je od kupca onda je issuer timestamp

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false, length=18)
    private String panPrimaoca;

    @Column(length=18)
    private String panPosaljioca;

    @Column(nullable = false)
    private Float iznos;

    @Column(nullable = false)
    private String successURL;

    @Column(nullable = false)
    private String failedURL;

    @Column(nullable = false)
    private String errorURL;
    
    @Column(nullable = false)
    //ovo pise u specifikaciji da se salje sa KP
    private Long merchantOrderId; //id transakcije sa NC

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date merchantTimestamp; //timestamp NC transakcije

    @Column
    private Long issuerOrderId;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date issuerTimestamp;


    public Transakcija() {
    }

    public Transakcija(Klijent uplacuje, Klijent prima, String paymentURL, Date timestamp, Status status, String panPrimaoca, String panPosaljioca, Float iznos, String successURL, String failedURL, String errorURL, Long merchantOrderId, Date merchantTimestamp) {
        this.uplacuje = uplacuje;
        this.prima = prima;
        this.paymentURL = paymentURL;
        this.timestamp = timestamp;
        this.status = status;
        this.panPrimaoca = panPrimaoca;
        this.panPosaljioca = panPosaljioca;
        this.iznos = iznos;
        this.successURL = successURL;
        this.failedURL = failedURL;
        this.errorURL = errorURL;
        this.merchantOrderId = merchantOrderId;
        this.merchantTimestamp = merchantTimestamp;
    }

    public Transakcija(Transakcija original) {
        this.uplacuje = original.uplacuje;
        this.prima = original.prima;
        this.paymentURL = original.paymentURL;
        this.timestamp = new Date();
        this.status = original.status;
        this.panPrimaoca = original.panPrimaoca;
        this.panPosaljioca = original.panPosaljioca;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date vremeKreiranja) {
        this.timestamp = vremeKreiranja;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPanPrimaoca() {
        return panPrimaoca;
    }

    public void setPanPrimaoca(String panPrimaoca) {
        this.panPrimaoca = panPrimaoca;
    }

    public String getPanPosaljioca() {
        return panPosaljioca;
    }

    public void setPanPosaljioca(String panPosaljioca) {
        this.panPosaljioca = panPosaljioca;
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

    public Date getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(Date dateTime) {
        this.merchantTimestamp = dateTime;
    }

    public Long getIssuerOrderId() {
        return issuerOrderId;
    }

    public void setIssuerOrderId(Long issuerOrderId) {
        this.issuerOrderId = issuerOrderId;
    }

    public Date getIssuerTimestamp() {
        return issuerTimestamp;
    }

    public void setIssuerTimestamp(Date issuerTimestamp) {
        this.issuerTimestamp = issuerTimestamp;
    }
}

