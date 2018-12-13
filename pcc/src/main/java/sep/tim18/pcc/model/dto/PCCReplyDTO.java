package sep.tim18.pcc.model.dto;

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
}



