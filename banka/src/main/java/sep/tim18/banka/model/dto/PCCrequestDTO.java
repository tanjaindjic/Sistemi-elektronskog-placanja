package sep.tim18.banka.model.dto;

import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;

public class PCCrequestDTO {
    //TODO proveriti da li treba nova transakcija ili za postojecu saljem i samo menjam status na CEKANJE
    private Long acquirer_order_id;

    private DateTime acquirer_timestamp;

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

    public PCCrequestDTO() {
    }

    public Long getAcquirer_order_id() {
        return acquirer_order_id;
    }

    public void setAcquirer_order_id(Long acquirer_order_id) {
        this.acquirer_order_id = acquirer_order_id;
    }

    public DateTime getAcquirer_timestamp() {
        return acquirer_timestamp;
    }

    public void setAcquirer_timestamp(DateTime acquirer_timestamp) {
        this.acquirer_timestamp = acquirer_timestamp;
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
