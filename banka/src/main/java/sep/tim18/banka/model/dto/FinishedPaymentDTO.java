package sep.tim18.banka.model.dto;

import java.util.Date;

import sep.tim18.banka.model.PaymentInfo;
import sep.tim18.banka.model.Transakcija;
import sep.tim18.banka.model.enums.Status;

public class FinishedPaymentDTO {

    private Status statusTransakcije;

    private Long merchantOrderID; //ono sto je poslao prodavac u prvom zahtevu

    private Long acquirerOrderID; //isto kao gore ako je ista banka ili drugi id od kreirane druge transakcije

    private Date acquirerTimestamp; //-||-

    private Long paymentID; //id pocetne transakcije

    private String redirectURL; //ili je success ili failed url

    @Override
	public String toString() {
		return "FinishedPaymentDTO [statusTransakcije=" + statusTransakcije + ", merchantOrderID=" + merchantOrderID
				+ ", acquirerOrderID=" + acquirerOrderID + ", acquirerTimestamp=" + acquirerTimestamp + ", paymentID="
				+ paymentID + ", redirectURL=" + redirectURL + "]";
	}

	public FinishedPaymentDTO() {
    }

    public Status getStatusTransakcije() {
        return statusTransakcije;
    }

    public void setStatusTransakcije(Status statusTransakcije) {
        this.statusTransakcije = statusTransakcije;
    }

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

    public Date getAcquirerTimestamp() {
        return acquirerTimestamp;
    }

    public void setAcquirerTimestamp(Date acquirerTimestamp) {
        this.acquirerTimestamp = acquirerTimestamp;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
