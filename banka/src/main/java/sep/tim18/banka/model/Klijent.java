package sep.tim18.banka.model;

import javax.persistence.GeneratedValue;

public class Klijent {

    @GeneratedValue
    private Long id;

    private String ime;
    private String prezime;
    private String merchantID;
    private String merchantPass;
    private String email;

    public Klijent() {
    }

    public Klijent(String ime, String prezime, String merchantID, String merchantPass, String email) {
        this.ime = ime;
        this.prezime = prezime;
        this.merchantID = merchantID;
        this.merchantPass = merchantPass;
        this.email = email;
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
}
