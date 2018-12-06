package sep.tim18.banka.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Klijent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = true, length=90)
    private String ime;

    @Column(nullable = true, length=90)
    private String prezime;

    @Column(nullable = true, length=30)
    private String merchantID;

    @Column(nullable = true, length=100)
    private String merchantPass;

    @Column(nullable = false, length=90)
    private String email;
    
    @OneToMany
    private List<Kartica> kartice;

    public Klijent() {
    }

    public Klijent(String ime, String prezime, String merchantID, String merchantPass, String email, List<Kartica> kartice) {
        this.ime = ime;
        this.prezime = prezime;
        this.merchantID = merchantID;
        this.merchantPass = merchantPass;
        this.email = email;
        this.kartice = kartice;
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

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantPass() {
        return merchantPass;
    }

    public void setMerchantPass(String merchantPass) {
        this.merchantPass = merchantPass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Kartica> getKartice() {
        return kartice;
    }

    public void setKartice(List<Kartica> kartice) {
        this.kartice = kartice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
