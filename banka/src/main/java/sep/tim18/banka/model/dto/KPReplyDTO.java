package sep.tim18.banka.model.dto;

import org.joda.time.DateTime;
import sep.tim18.banka.model.enums.Status;

public class KPReplyDTO {

    private Long merchantOrderID;
    private Long acquirerOrderID;
    private DateTime acquirerTimestamp;
    private Long paymentID;
    private Status status;

    public KPReplyDTO() { }

    public Long getMerchantOrderID() {
        return merchantOrderID;
    }

    public void setMerchantOrderID(Long merchantOrderID) {
        this.merchantOrderID = merchantOrderID;
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

    public Long getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
