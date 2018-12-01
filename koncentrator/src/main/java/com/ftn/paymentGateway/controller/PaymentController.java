package com.ftn.paymentGateway.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/")
public class PaymentController {
	
	@RequestMapping(value = "test", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> testMe() {
		
		return new ResponseEntity<String>("Uspelooo", HttpStatus.OK);
	}

}
