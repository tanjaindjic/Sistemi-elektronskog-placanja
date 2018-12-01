package sep.tim18.banka.model.dto;

import java.sql.Time;
import java.sql.Timestamp;

public class RequestDTO {
    private String merchantID;
    private String merchantPass;
    private Float iznos;
    private int merchantOrderID;
    private Timestamp merchantTimestamp;
    private String successURL;
    private String failedURL;
    private String errorURL;

    public RequestDTO() {
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

    public Float getIznos() {
        return iznos;
    }

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    public int getMerchantOrderID() {
        return merchantOrderID;
    }

    public void setMerchantOrderID(int merchantOrderID) {
        this.merchantOrderID = merchantOrderID;
    }

    public Timestamp getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(Timestamp merchantTimestamp) {
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
