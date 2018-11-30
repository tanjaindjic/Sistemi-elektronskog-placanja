package sep.tim18.banka.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;

public class Kartica {
    @GeneratedValue
    private long id;

    @Length(min = 16, max = 16)
    private String pan;

    @Length(min = 3, max = 4)
    private String ccv;

    @Length(min = 4, max = 4)
    private String expDate;

    @Length(min = 5, max = 18)
    private String brRacuna;

    private Float raspolozivaSredstva;

    private Float rezervisanaSredstva;

    public Kartica() {
    }

    public Kartica(@Length(min = 16, max = 16) String pan, @Length(min = 3, max = 4) String ccv, @Length(min = 4, max = 4) String expDate, @Length(min = 5, max = 18) String brRacuna, Float raspolozivaSredstva, Float rezervisanaSredstva) {
        this.pan = pan;
        this.ccv = ccv;
        this.expDate = expDate;
        this.brRacuna = brRacuna;
        this.raspolozivaSredstva = raspolozivaSredstva;
        this.rezervisanaSredstva = rezervisanaSredstva;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getBrRacuna() {
        return brRacuna;
    }

    public void setBrRacuna(String brRacuna) {
        this.brRacuna = brRacuna;
    }

    public Float getRaspolozivaSredstva() {
        return raspolozivaSredstva;
    }

    public void setRaspolozivaSredstva(Float raspolozivaSredstva) {
        this.raspolozivaSredstva = raspolozivaSredstva;
    }

    public Float getRezervisanaSredstva() {
        return rezervisanaSredstva;
    }

    public void setRezervisanaSredstva(Float rezervisanaSredstva) {
        this.rezervisanaSredstva = rezervisanaSredstva;
    }
}
