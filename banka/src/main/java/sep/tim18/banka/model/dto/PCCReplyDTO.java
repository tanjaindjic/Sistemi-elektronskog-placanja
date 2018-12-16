package sep.tim18.banka.model.dto;

import sep.tim18.banka.model.enums.Status;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class PCCReplyDTO {

    private Long issuerOrderID;
    private Date issuerTimestamp;
    @NotNull
    private Status status;
    @NotNull
    private Long acquirerOrderID;

    public PCCReplyDTO() {
    }


    public Long getAcquirerOrderID() {
        return acquirerOrderID;
    }

    public void setAcquirerOrderID(Long acquirerOrderID) {
        this.acquirerOrderID = acquirerOrderID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public Long getIssuerOrderID() {
        return issuerOrderID;
    }

    public void setIssuerOrderID(Long issuerOrderID) {
        this.issuerOrderID = issuerOrderID;
    }

    public Date getIssuerTimestamp() {
        return issuerTimestamp;
    }

    public void setIssuerTimestamp(Date issuerTimestamp) {
        this.issuerTimestamp = issuerTimestamp;
    }
   
    @Override
    public String toString() {
        return "PCCReplyDTO{" +
                "issuerOrderID=" + issuerOrderID +
                ", issuerTimestamp=" + issuerTimestamp +
                ", status=" + status +
                ", acquirerOrderID=" + acquirerOrderID +
                '}';
    }
}



