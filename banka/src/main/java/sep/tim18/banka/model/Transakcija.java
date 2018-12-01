package sep.tim18.banka.model;

import org.hibernate.validator.constraints.Length;
import sep.tim18.banka.model.enums.Status;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.joda.time.DateTime;

@Entity
public class Transakcija {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    private Klijent uplacuje;

    @ManyToOne
    private Klijent prima;

    private String paymentURL;

    private DateTime vremeKreiranja;

    private DateTime vremeIzvrsenja;

    private Status status;

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

    @Length(max=18)
    private String racunPrimaoca;

    @Length(max=18)
    private String racunPosiljaoca;

    private Float iznos;

    public Transakcija() {
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
}

