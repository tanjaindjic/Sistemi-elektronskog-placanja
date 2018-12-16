package sep.tim18.banka.model;

import javax.persistence.*;

@Entity
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentID;

    @Column(nullable = false, length=256)
    private String paymentURL;

    @OneToOne
    private Transakcija transakcija;

    public PaymentInfo() {
    }

    public PaymentInfo(Transakcija t, String token) {
        this.transakcija = t;
        this.paymentURL = token;
    }


    public Long getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
    }

    public Transakcija getTransakcija() {
        return transakcija;
    }

    public void setTransakcija(Transakcija transakcija) {
        this.transakcija = transakcija;
    }
}
