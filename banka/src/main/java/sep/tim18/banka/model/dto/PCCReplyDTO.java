package sep.tim18.banka.model.dto;

import org.joda.time.DateTime;

public class PCCReplyDTO {

    private Long acquirerOrderID;
    private DateTime acquirerTimestamp;
    private Long issuerOrderID;
    private DateTime issuerTimestamp;

    public PCCReplyDTO() {
    }

    public Long getAcquirerOrderID() {
        return acquirerOrderID;
    }

    public void setAcquirerOrderID(Long acquirerOrderID) {
        this.acquirerOrderID = acquirerOrderID;
    }

    public DateTime getAcquirerTimestamp() {
        return acquirerTimestamp;
    }

    public void setAcquirerTimestamp(DateTime acquirerTimestamp) {
        this.acquirerTimestamp = acquirerTimestamp;
    }

    public Long getIssuerOrderID() {
        return issuerOrderID;
    }

    public void setIssuerOrderID(Long issuerOrderID) {
        this.issuerOrderID = issuerOrderID;
    }

    public DateTime getIssuerTimestamp() {
        return issuerTimestamp;
    }

    public void setIssuerTimestamp(DateTime issuerTimestamp) {
        this.issuerTimestamp = issuerTimestamp;
    }
}



