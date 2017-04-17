package com.synchronoss.saw.composite.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.synchronoss.saw.composite.exceptions.AnalyzeModuleSAWException;
import com.synchronoss.saw.composite.exceptions.CategoriesSAWException;
import com.synchronoss.saw.composite.exceptions.CommonModuleSAWException;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.Payload;

import rx.Single;

/**
 * This interface will define the specification<br/>
 * for composite layer mediation API for the sub-systems.
 * @author saurav.paul
 * @version 1.0
 */
public interface SAWCompositeServiceInterface {
	
	
	
	public Single<ResponseEntity<Payload>> defaultAnalyze(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;

	public ResponseEntity<Payload> menuItems(@RequestBody Payload payload) throws CommonModuleSAWException, JSONValidationSAWException;
	
	public ResponseEntity<Payload> newAnalysis(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;

	public ResponseEntity<Payload> createAnalysis(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;
	
	public ResponseEntity<Payload> listOfCategories(@RequestBody Payload payload) throws CategoriesSAWException, JSONValidationSAWException;

	public ResponseEntity<Payload> saveAnalysis(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;
	
	public ResponseEntity<Payload> applySort(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;
	
	public ResponseEntity<Payload> applyfiter(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;
	
	public ResponseEntity<Payload> analyzeByType(@RequestBody Payload payload) throws AnalyzeModuleSAWException, JSONValidationSAWException;
}

