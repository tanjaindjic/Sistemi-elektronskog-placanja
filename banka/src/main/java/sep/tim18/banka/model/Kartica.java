package sep.tim18.banka.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
public class Kartica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Length(min = 8, max = 19)
    private String pan;

    @Length(min = 3, max = 4)
    private String ccv;

    private String expDate;

    private String brRacuna;

    private Float raspolozivaSredstva;

    private Float rezervisanaSredstva;

    @ManyToOne
    private Klijent vlasnik;

    public Kartica() {
    }

    public Kartica(@Length(min = 8, max = 19) String pan, @Length(min = 3, max = 4) String ccv, String expDate, String brRacuna, Float raspolozivaSredstva, Float rezervisanaSredstva, Klijent vlasnik) {
        this.pan = pan;
        this.ccv = ccv;
        this.expDate = expDate;
        this.brRacuna = brRacuna;
        this.raspolozivaSredstva = raspolozivaSredstva;
        this.rezervisanaSredstva = rezervisanaSredstva;
        this.vlasnik = vlasnik;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Klijent getVlasnik() {
        return vlasnik;
    }

    public void setVlasnik(Klijent vlasnik) {
        this.vlasnik = vlasnik;
    }
}
