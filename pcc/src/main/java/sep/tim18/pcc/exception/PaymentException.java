package sep.tim18.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class PaymentException extends Exception {
    private static final long serialVersionUID = 1L;


    public PaymentException(String s) {
        super(s);
    }
}
