package com.synchronoss.saw.composite.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.synchronoss.saw.composite.exceptions.AnalyzeModuleSAWException;
import com.synchronoss.saw.composite.exceptions.CommonModuleSAWException;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.RoutingPayload;

/**
 * This interface will define the specification<br/>
 * for composite layer mediation API for the sub-systems.
 * 
 * @author saurav.paul
 * @version 1.0
 */
public interface SAWCompositeServiceInterface {



  public RoutingPayload menuItems(@RequestBody RoutingPayload payload)
      throws CommonModuleSAWException, JSONValidationSAWException;

  public RoutingPayload newAnalysis(@RequestBody RoutingPayload payload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;

  public RoutingPayload createAnalysis(@RequestBody RoutingPayload payload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;

  public ResponseEntity<RoutingPayload> saveAnalysis(@RequestBody RoutingPayload RoutingPayload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;

  public ResponseEntity<RoutingPayload> applySort(@RequestBody RoutingPayload payload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;

  public ResponseEntity<RoutingPayload> applyfiter(@RequestBody RoutingPayload payload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;

  public ResponseEntity<RoutingPayload> analyzeByType(@RequestBody RoutingPayload payload)
      throws AnalyzeModuleSAWException, JSONValidationSAWException;
}

