package com.synchronoss.saw.composite.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.api.SAWSecurityServiceInterface;

/**
 * This class is the integration & universal response object<br>
 * to the user interface. This class is the implementation of actual<br>
 * integrated API<br>
 * @author saurav.paul
 * @version 1.0
 */

// TODO : Inject two interfaces
// TODO : Swagger
// TODO : Non Blocking asynchronous implementation
// TODO : Integration flow as designed in the white Paper

@RestController
public class CompositeSAWController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CompositeSAWController.class);

	@Autowired
	private SAWSecurityServiceInterface sawSecurityServiceInterface;
	
	@Autowired
	@Qualifier("saw-composite-api")
	private SAWCompositeServiceInterface sawCompositeServiceInterface;
	
}
