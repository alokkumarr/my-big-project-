package com.synchronoss.saw.composite.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.synchronoss.saw.composite.SAWCompositeProperties;
import com.synchronoss.saw.composite.ServiceUtils;
import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.aspects.RoutingLogging;
import com.synchronoss.saw.composite.exceptions.AnalyzeModuleSAWException;
import com.synchronoss.saw.composite.exceptions.CategoriesSAWException;
import com.synchronoss.saw.composite.exceptions.CommonModuleSAWException;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.Contents;
import com.synchronoss.saw.composite.model.Contents.Action;
import com.synchronoss.saw.composite.model.Keys;
import com.synchronoss.saw.composite.model.Payload;
import com.synchronoss.saw.composite.model.RoutingPayload;

import rx.Single;

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
	
	//@LoadBalanced // TODO: This cannot be used because /md, /exe & /semantics services are not registered with Eureka
	private final RestTemplate restTemplate;

    @Autowired
    ServiceUtils util;

    public SAWCompositeServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@HystrixCommand(fallbackMethod = "defaultAnalyzeFallBack")
	@RoutingLogging
	@Override
	public Single<ResponseEntity<Payload>> defaultAnalyze(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		ResponseEntity<Payload> combinedPayload = null;
		try {
			if (!util.jsonSchemaValidate(payload.toString(), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			// module, customer_code, role_type, data_security_key as part of
			// content, type=menu
			// verify the input with preconditions
			combinedPayload = restTemplate.getForEntity(util.getClassPathResources("/fallback/menu_structure.json").getURI(), Payload.class);	
		
		} 
		catch (HttpClientErrorException ex) {
			if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
				throw new AnalyzeModuleSAWException("Exception occured " + ex.getMessage());
			}
		} catch (IOException ex) {
			throw new JSONValidationSAWException("Exception occured " + ex.getMessage());
		} catch (ProcessingException ex) {
			throw new JSONValidationSAWException("Exception occured " + ex.getMessage());
		}
		return Single.just(new ResponseEntity<>(combinedPayload.getBody(), HttpStatus.FOUND));
	}
	
	@RoutingLogging
	public DeferredResult<Payload> defaultAnalyzeFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		
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

	@HystrixCommand(fallbackMethod = "applySortFallback")
	@Override
	public ResponseEntity<Payload> applySort(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> applySortFallback(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	
	@HystrixCommand(fallbackMethod = "applyfilterFallback")	
	@Override
	public ResponseEntity<Payload> applyfiter(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> applyfilterFallback(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	@HystrixCommand(fallbackMethod = "analyzeByTypeFallBack")
	@Override
	public ResponseEntity<Payload> analyzeByType(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ResponseEntity<Payload> analyzeByTypeFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) throws IOException, ProcessingException {
		RoutingPayload routingPayload = new RoutingPayload();
		Contents contents = new Contents();
		contents.setAction(Action.READ);
		Keys keys = new Keys();
		keys.setAdditionalProperty("type", "menu");
		keys.setAdditionalProperty("module", "Analyze");
		contents.setKeys(keys);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		routingPayload.setContents(contents);
		List<Object> data = new ArrayList<Object>();
		data.add("Data");
		routingPayload.setData(data);
		System.out.println(objectMapper.writeValueAsString(routingPayload));
		
	}
	
}
