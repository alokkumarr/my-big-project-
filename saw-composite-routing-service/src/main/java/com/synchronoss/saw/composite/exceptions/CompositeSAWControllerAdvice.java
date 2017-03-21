package com.synchronoss.saw.composite.exceptions;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
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
 * 
 * @author saurav.paul
 *
 */
@ControllerAdvice
@RequestMapping(produces = "application/vnd.error")
@ResponseBody
public class CompositeSAWControllerAdvice {

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(FileNotFoundException.class)
	public VndErrors fileNotFoundException(FileNotFoundException ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}
	
	@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
	@ExceptionHandler({DataAccessException.class, SecurityModuleSAWException.class, Exception.class})
	public VndErrors  globalException(Exception ex) {
		return this.error(ex, ex.getLocalizedMessage());
	}

	private <E extends Exception> VndErrors error(E e, String logref) {
		String msg = Optional.of(e.getMessage()).orElse(e.getClass().getSimpleName());
		return new VndErrors(logref, msg);
	}
}
