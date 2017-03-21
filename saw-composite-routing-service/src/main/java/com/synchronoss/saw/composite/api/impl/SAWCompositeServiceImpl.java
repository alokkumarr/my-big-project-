package com.synchronoss.saw.composite.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.synchronoss.saw.composite.SAWCompositeProperties;
import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.exceptions.AnalyzeModuleSAWException;
import com.synchronoss.saw.composite.exceptions.CategoriesSAWException;
import com.synchronoss.saw.composite.exceptions.CommonModuleSAWException;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.Payload;

/**
 * This class is the integration & universal response object<br>
 * for the actual independent API implementation <br>
 * This class is the implementation of actual<br>
 * @author saurav.paul
 * @version 1.0
 */

// TODO : Implement the API independently
// TODO : Non Blocking asynchronous implementation using RXJava
// TODO : Implement the fallback methods

@Service
@Qualifier("saw-composite-api")
public class SAWCompositeServiceImpl implements SAWCompositeServiceInterface {
	
	private static final Logger LOG = LoggerFactory.getLogger(SAWCompositeServiceImpl.class);

	@Autowired
	private SAWCompositeProperties sawCompositeProperties;
	

	@HystrixCommand(fallbackMethod = "defaultAnalyzeFallBack")
	@Override
	public ResponseEntity<Payload> defaultAnalyze(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public ResponseEntity<Payload> defaultAnalyzeFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	

	@HystrixCommand(fallbackMethod = "menuItemsFallBack")
	@Override
	public ResponseEntity<Payload> menuItems(Payload payload)
			throws CommonModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ResponseEntity<Payload> menuItemsFallBack(Payload payload)
			throws CommonModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@HystrixCommand(fallbackMethod = "newAnalysisFallBack")
	@Override
	public ResponseEntity<Payload> newAnalysis(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> newAnalysisFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@HystrixCommand(fallbackMethod = "listOfCategoriesFallBack")
	@Override
	public ResponseEntity<Payload> listOfCategories(Payload payload)
			throws CategoriesSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> listOfCategoriesFallBack(Payload payload)
			throws CategoriesSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@HystrixCommand(fallbackMethod = "createAnalysisFallBack")
	@Override
	public ResponseEntity<Payload> createAnalysis(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> createAnalysisFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@HystrixCommand(fallbackMethod = "saveAnalysisFallBack")
	@Override
	public ResponseEntity<Payload> saveAnalysis(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> saveAnalysisFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
