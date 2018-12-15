package sep.tim18.banka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class PaymentException extends Exception {
    private static final long serialVersionUID = 1L;

    public PaymentException(String s) {
        super("Nije moguce izvrsiti naplatu.");
        PaymentExceptionHandler(s);
    }

    private ResponseEntity<Map> PaymentExceptionHandler(String s) {
        Map<String, String> map = new HashMap<>();
        map.put("Location", "/failed");
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}
