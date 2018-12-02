package sep.tim18.banka.model.dto;

import org.hibernate.validator.constraints.Length;

public class PaymentDTO {

    @Length(min = 8, max = 19)
    private String pan;

    @Length(min = 3, max = 4)
    private String cvv;

    @Length(min = 1)
    private String ime;

    @Length(min = 1)
    private String prezime;

    private int mesec;

    private int godina;

    public PaymentDTO() {
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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

    public int getMesec() {
        return mesec;
    }

    public void setMesec(int mesec) {
        this.mesec = mesec;
    }

    public int getGodina() {
        return godina;
    }

    public void setGodina(int godina) {
        this.godina = godina;
    }
}
