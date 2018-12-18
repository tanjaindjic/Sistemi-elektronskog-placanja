package sep.tim18.pcc.model;

import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import sep.tim18.pcc.model.enums.Status;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Zahtev {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long acquirerOrderID; //id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev na pcc

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquirerTimestamp;

    private Long issuerOrderID; //id transkacije kreirane u banci prodavca koja se referencira na ovaj zahtev na pcc

    @Temporal(TemporalType.TIMESTAMP)
    private Date issuerTimestamp;

    @ManyToOne
    private Banka bankaProdavca;

    @ManyToOne
    private Banka bankaKupca;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vremeKreiranja;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String returnURL;

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

    public Date getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(Date vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public Date getAcquirerTimestamp() {
        return acquirerTimestamp;
    }

    public void setAcquirerTimestamp(Date acquirerTimestamp) {
        this.acquirerTimestamp = acquirerTimestamp;
    }

    public Date getIssuerTimestamp() {
        return issuerTimestamp;
    }

    public void setIssuerTimestamp(Date issuerTimestamp) {
        this.issuerTimestamp = issuerTimestamp;
    }
}
