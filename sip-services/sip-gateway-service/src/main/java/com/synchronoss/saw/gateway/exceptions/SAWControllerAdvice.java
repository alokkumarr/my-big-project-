package com.synchronoss.saw.gateway.exceptions;

import java.util.Optional;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import com.synchronoss.bda.sip.exception.SipNotProcessedSipEntityException;

/**
 * This class refers to global exception<br/>
 * handler for composite or mediation layer<br/>
 * If any piece of code throws the below any of the exception<br>
 * will return HATEOS complaint objects<br>
 * 
 * @author saurav.paul
 *
 */
@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
@ResponseBody
public class SAWControllerAdvice {

	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	@ExceptionHandler(TokenMissingSAWException.class)
	public VndErrors tokenMissingSAWException(TokenMissingSAWException ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpServerErrorException.class)
    public VndErrors inValidRequest(HttpServerErrorException ex) {
        return this.error(ex, ex.getLocalizedMessage());
    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(SipNotProcessedSipEntityException.class)
    public VndErrors inValidJsonBody(SipNotProcessedSipEntityException ex) {
        return this.error(ex, ex.getLocalizedMessage());
    }

	private <E extends Exception> VndErrors error(E e, String logref) {
		String msg = Optional.of(e.getMessage()).orElse(e.getClass().getSimpleName());
		return new VndErrors(logref, msg);
	}
}
