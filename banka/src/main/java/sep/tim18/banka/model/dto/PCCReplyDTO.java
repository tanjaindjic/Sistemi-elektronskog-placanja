package sep.tim18.banka.model.dto;

import java.util.Date;

public class PCCReplyDTO {

    private Long issuerOrderID;
    private Date issuerTimestamp;

    public PCCReplyDTO() {
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
                '}';
    }
}



