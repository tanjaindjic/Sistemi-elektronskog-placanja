package sep.tim18.pcc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Banka {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String brojBanke;

    private String urlBanke;//gde saljemo za naplatu

    public Banka(String brojBanke, String urlBanke) {
        this.brojBanke = brojBanke;
        this.urlBanke = urlBanke;
    }

    public Banka() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrojBanke() {
        return brojBanke;
    }

    public void setBrojBanke(String brojBanke) {
        this.brojBanke = brojBanke;
    }

    public String getUrlBanke() {
        return urlBanke;
    }

    public void setUrlBanke(String urlBanke) {
        this.urlBanke = urlBanke;
    }
}
