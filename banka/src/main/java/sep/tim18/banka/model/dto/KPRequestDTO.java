package sep.tim18.banka.model.dto;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class KPRequestDTO {
    @NotNull
    private String merchantID;
    @NotNull
    private String merchantPass;
    @NotNull
    private Float iznos;
    @NotNull
    private Long merchantOrderID;
    @NotNull
    private Date merchantTimestamp;
    private String successURL;
    private String failedURL;
    private String errorURL;

    public KPRequestDTO() {
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    @Override
    public String toString() {
        return "KPRequestDTO{" +
                "merchantID='" + merchantID + '\'' +
                ", merchantPass='" + merchantPass + '\'' +
                ", iznos=" + iznos +
                ", merchantOrderID=" + merchantOrderID +
                ", merchantTimestamp=" + merchantTimestamp +
                ", successURL='" + successURL + '\'' +
                ", failedURL='" + failedURL + '\'' +
                ", errorURL='" + errorURL + '\'' +
                '}';
    }

    public String getMerchantPass() {
        return merchantPass;
    }

    public void setMerchantPass(String merchantPass) {
        this.merchantPass = merchantPass;
    }

    public Float getIznos() {
        return iznos;
    }

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    public Long getMerchantOrderID() {
        return merchantOrderID;
    }

    public void setMerchantOrderID(Long merchantOrderID) {
        this.merchantOrderID = merchantOrderID;
    }

    public Date getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(Date merchantTimestamp) {
        this.merchantTimestamp = merchantTimestamp;
    }

    public String getSuccessURL() {
        return successURL;
    }

    public void setSuccessURL(String successURL) {
        this.successURL = successURL;
    }

    public String getFailedURL() {
        return failedURL;
    }

    public void setFailedURL(String failedURL) {
        this.failedURL = failedURL;
    }

    public String getErrorURL() {
        return errorURL;
    }

    public void setErrorURL(String errorURL) {
        this.errorURL = errorURL;
    }
}
