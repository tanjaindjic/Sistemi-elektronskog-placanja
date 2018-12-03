package sep.tim18.pcc.model;

import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import sep.tim18.pcc.model.enums.Status;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class Zahtev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long acquirerOrderID; //id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev na pcc

    private Long issuerOrderID; //id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev na pcc

    @ManyToOne
    private Banka bankaProdavca;

    @ManyToOne
    private Banka bankaKupca;

    private DateTime vremeKreiranja;

    private Status status;

    public Zahtev() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getAcquirerOrderID() {
        return acquirerOrderID;
    }

    public void setAcquirerOrderID(Long acquirerOrderID) {
        this.acquirerOrderID = acquirerOrderID;
    }

    public Long getIssuerOrderID() {
        return issuerOrderID;
    }

    public void setIssuerOrderID(Long issuerOrderID) {
        this.issuerOrderID = issuerOrderID;
    }

    public Banka getBankaProdavca() {
        return bankaProdavca;
    }

    public void setBankaProdavca(Banka bankaProdavca) {
        this.bankaProdavca = bankaProdavca;
    }

    public Banka getBankaKupca() {
        return bankaKupca;
    }

    public void setBankaKupca(Banka bankaKupca) {
        this.bankaKupca = bankaKupca;
    }

    public DateTime getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(DateTime vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
