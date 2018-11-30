package sep.tim18.banka.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import java.sql.Date;

public class Transakcija {

    @GeneratedValue
    private Long id;

    private Klijent uplacuje;

    private Klijent prima;

    private String paymentURL;

    private Date vremeKreiranja;

    private Date vremeIzvrsenja;

    private Status status;

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

    public Date getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(Date vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public Date getVremeIzvrsenja() {
        return vremeIzvrsenja;
    }

    public void setVremeIzvrsenja(Date vremeIzvrsenja) {
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

