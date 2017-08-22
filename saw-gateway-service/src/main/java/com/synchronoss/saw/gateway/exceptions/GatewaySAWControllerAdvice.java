package com.synchronoss.saw.gateway.exceptions;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
public class GatewaySAWControllerAdvice {

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(FileNotFoundException.class)
	public VndErrors fileNotFoundException(FileNotFoundException ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}
	
	@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
	@ExceptionHandler({SecurityModuleSAWException.class})
	public VndErrors  globalException(Exception ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ CommonModuleSAWException.class,
		TokenMissingSAWException.class, TokenValidationSAWException.class,IllegalStateException.class,
		Exception.class})
	public VndErrors  badRequestException(Exception ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}


	
	
	private <E extends Exception> VndErrors error(E e, String logref) {
		String msg = Optional.of(e.getMessage()).orElse(e.getClass().getSimpleName());
		return new VndErrors(logref, msg);
	}
}
