package com.synchronoss.saw.composite.api.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.assertj.core.util.Preconditions;
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
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.composite.ServiceUtils;
import com.synchronoss.saw.composite.api.SAWCompositeServiceInterface;
import com.synchronoss.saw.composite.exceptions.AnalyzeModuleSAWException;
import com.synchronoss.saw.composite.exceptions.CommonModuleSAWException;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.Key;
import com.synchronoss.saw.composite.model.Payload;
import com.synchronoss.saw.composite.model.RoutingPayload;

/**
 * This class is the integration & universal response object<br>
 * for the actual independent API implementation <br>
 * This class is the implementation of actual<br>
 * Before getting into this service, the validation for<br>
 * for the service will be done
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

	//@Autowired
	//private SAWCompositeProperties sawCompositeProperties;
	
	//@LoadBalanced 
	// TODO: This cannot be used because /md, /exe & /semantics services 
	// are not registered with Eureka
	private final RestTemplate restTemplate;

	@Autowired
    private ServiceUtils util;

    @Autowired
    public SAWCompositeServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	//@HystrixCommand(fallbackMethod = "menuItemsFallBack")
	@Override
	public RoutingPayload menuItems(RoutingPayload payload)
			throws CommonModuleSAWException, JSONValidationSAWException {

		// module = "analyze"
		// customerCode = "",
		// type = "menu"
		LOG.info("method menuItems() execution started.");
		
		RoutingPayload response = null;
		try {
			util.checkKeysConstraints(payload, "module");
			util.checkKeysConstraints(payload, "type");
			util.checkKeysConstraints(payload, "customerCode");
		} catch (NullPointerException | JSONValidationSAWException ex) {
			throw new JSONValidationSAWException(ex.getMessage());
		}		
		List<Key> items = payload.getContents().getKeys();
		items.forEach(item->
		{
			String module = item.getAdditionalProperties().get("module").toString();
			String type  = item.getAdditionalProperties().get("type").toString();
			if (util.checkContraints(module) == false && util.checkContraints(type) == false)
			{
				throw new JSONValidationSAWException("provided value in 'module' or 'type' is not accepted by service");
			}
		});

		try {
			if (!util.jsonSchemaValidate(util.getMapper().writeValueAsString(payload), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			// The below line is commented it will be activated when actual API will be active
			/*	HttpEntity<RoutingPayload> request = new HttpEntity<>(payload);
						response = restTemplate.exchange(sawCompositeProperties.getMetaDataURL() + sawCompositeProperties.getMetaDataContext(), 
								HttpMethod.POST, request, RoutingPayload.class); */
			response = 
					util.getMapper().readValue(new File(util.getClassPathResources("/fallback/menu/menu_structure_response.json").
							getURI().getPath()), RoutingPayload.class);
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
		
		LOG.info("method menuItems() execution ended.");
		return response;
	}

	/**
	 * This method is fall back method of menuItems
	 * @param payload
	 * @return ResponseEntity<RoutingPayload>
	 * @throws CommonModuleSAWException
	 * @throws JSONValidationSAWException
	 */
	public ResponseEntity<RoutingPayload> menuItemsFallBack(RoutingPayload payload)
			throws CommonModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//@HystrixCommand(fallbackMethod = "newAnalysisFallBack")
	@Override
	public RoutingPayload newAnalysis(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// _id,
		// type = semantic
		// module = ANALYZE
		
		LOG.info("method newAnalysis() execution started.");
		RoutingPayload response = null;
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("customerCode").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("customerCode").toString() !=null), 
				"_id attribute in the request body cannot be null or empty");
		
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("dataSecurityKey").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("dataSecurityKey").toString() !=null), 
				"type attribute in the request body cannot be null or empty");
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("type").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("type").toString() !=null), 
				"type attribute in the request body cannot be null or empty");

		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("module").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("module").toString() !=null), 
				"module attribute in the request body cannot be null or empty");

		try {
			if (!util.jsonSchemaValidate(util.getMapper().writeValueAsString(payload), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			
			// The below line is commented it will be activated when actual API will be active
			
/*			HttpEntity<RoutingPayload> request = new HttpEntity<>(payload);
			response = restTemplate.exchange(sawCompositeProperties.getMetaDataURL() + sawCompositeProperties.getMetaDataContext(), 
					HttpMethod.POST, request, RoutingPayload.class);
*/
			
			response = 
					util.getMapper().readValue(new File(util.getClassPathResources("/fallback/semantic/semantic_layer_data.json").
							getURI().getPath()), RoutingPayload.class);
		
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
		LOG.info("method newAnalysis() execution ended.");
		return response;
	}

	/**
	 * This method is fall back method for newAnalysis
	 * @param payload
	 * @return ResponseEntity<RoutingPayload>
	 * @throws AnalyzeModuleSAWException
	 * @throws JSONValidationSAWException
	 */
	public ResponseEntity<RoutingPayload> newAnalysisFallBack(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		
		return null;
	}
	
	
	//@HystrixCommand(fallbackMethod = "createAnalysisFallBack")
	@Override
	public RoutingPayload createAnalysis(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// data_security_key,
		// customer_code
		LOG.info("method createAnalysis() execution started.");
		int number_of_records = 0;
		RoutingPayload response = null;
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("data_security_key").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("data_security_key").toString() !=null), 
				"data_security_key attribute in the request body cannot be null or empty");
		
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("customer_code").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("customer_code").toString() !=null), 
				"customer_code attribute in the request body cannot be null or empty");
		
		number_of_records = Integer.parseInt(payload.getContents().getKeys().get(0).getAdditionalProperties().get("number_of_records").toString())==0 ? 10 : 
			Integer.parseInt(payload.getContents().getKeys().get(0).getAdditionalProperties().get("number_of_records").toString());
		payload.getContents().getKeys().get(0).getAdditionalProperties().put("number_of_records", String.valueOf(number_of_records));
		
		try {
			if (!util.jsonSchemaValidate(util.getMapper().writeValueAsString(payload), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			
			// The below line is commented it will be activated when actual API will be active
			
/*			HttpEntity<RoutingPayload> request = new HttpEntity<>(payload);
			response = restTemplate.exchange(sawCompositeProperties.getMetaDataURL() + sawCompositeProperties.getMetaDataContext(), 
					HttpMethod.POST, request, RoutingPayload.class);
*/
			
			response = 
					util.getMapper().readValue(new File(util.getClassPathResources("/fallback/semantic/semantic_layer_data.json").
							getURI().getPath()), RoutingPayload.class);
		
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
		LOG.info("method createAnalysis() execution ended.");
		return response;

		
	}

	/**
	 * This method is fall back method for create Analysis
	 * @param payload
	 * @return
	 * @throws AnalyzeModuleSAWException
	 * @throws JSONValidationSAWException
	 */
	public ResponseEntity<Payload> createAnalysisFallBack(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//@HystrixCommand(fallbackMethod = "saveAnalysisFallBack")
	@Override
	public ResponseEntity<RoutingPayload> saveAnalysis(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// data_security_key,
		// customer_code
		LOG.info("method saveAnalysis() execution started.");

		Preconditions.checkNotNull(payload);
		ResponseEntity<RoutingPayload> response = null;
		
		try {
			if (!util.jsonSchemaValidate(payload.toString(), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			
			// The below line is commented it will be activated when actual API will be active
			
/*			HttpEntity<RoutingPayload> request = new HttpEntity<>(payload);
			response = restTemplate.exchange(sawCompositeProperties.getMetaDataURL() + sawCompositeProperties.getMetaDataContext(), 
					HttpMethod.POST, request, RoutingPayload.class);
*/
			
			response = restTemplate.getForEntity(util.getClassPathResources("/fallback/tabular/tabular_type_data_new.json").getURI(), 
					RoutingPayload.class);	
		
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
		LOG.info("method saveAnalysis() execution ended.");
		return response;	
		}

	/**
	 * This method is fall back method for save analysis
	 * @param payload
	 * @return
	 * @throws AnalyzeModuleSAWException
	 * @throws JSONValidationSAWException
	 */
	public ResponseEntity<RoutingPayload> saveAnalysisFallBack(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO:
	//@HystrixCommand(fallbackMethod = "applySortFallback")
	@Override
	public ResponseEntity<RoutingPayload> applySort(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<RoutingPayload> applySortFallback(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO:
	//@HystrixCommand(fallbackMethod = "applyfilterFallback")	
	@Override
	public ResponseEntity<RoutingPayload> applyfiter(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Payload> applyfilterFallback(Payload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}

	//@HystrixCommand(fallbackMethod = "analyzeByTypeFallBack")
	@Override
	public ResponseEntity<RoutingPayload> analyzeByType(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// _id,
		// type,
		// module
		// number_of_records
		LOG.info("method analyzeByType() execution started.");
		int number_of_records = 0;
		ResponseEntity<RoutingPayload> response = null;
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("_id").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("_id").toString() !=null), 
				"_id attribute in the request body cannot be null or empty");
		
		
		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("type").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("type").toString() !=null), 
				"type attribute in the request body cannot be null or empty");

		Preconditions.checkArgument((!payload.getContents().getKeys().get(0).getAdditionalProperties().get("module").toString().equals("") ||
				payload.getContents().getKeys().get(0).getAdditionalProperties().get("type").toString() !=null), 
				"module attribute in the request body cannot be null or empty");
		
		number_of_records = Integer.parseInt(payload.getContents().getKeys().get(0).getAdditionalProperties().get("number_of_records").toString())==0 ? 10 : 
			Integer.parseInt(payload.getContents().getKeys().get(0).getAdditionalProperties().get("number_of_records").toString());
		payload.getContents().getKeys().get(0).getAdditionalProperties().put("number_of_records", String.valueOf(number_of_records));
		
		try {
			if (!util.jsonSchemaValidate(payload.toString(), util.SCHEMA_FILENAME)) {
				throw new JSONValidationSAWException("Request body is not valid or please verify the request body");
			}
			
			// The below line is commented it will be activated when actual API will be active
			
/*			HttpEntity<RoutingPayload> request = new HttpEntity<>(payload);
			response = restTemplate.exchange(sawCompositeProperties.getMetaDataURL() + sawCompositeProperties.getMetaDataContext(), 
					HttpMethod.POST, request, RoutingPayload.class);
*/
			
			response = restTemplate.getForEntity(util.getClassPathResources("/fallback/semantic_layer_data.json").getURI(), 
					RoutingPayload.class);	
		
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
		LOG.info("method analyzeByType() execution ended.");
		return response;

	}

	/**
	 * This method is fall back method for the to retrieve analyze by type & Id
	 * @param payload
	 * @return
	 * @throws AnalyzeModuleSAWException
	 * @throws JSONValidationSAWException
	 */
	public ResponseEntity<RoutingPayload> analyzeByTypeFallBack(RoutingPayload payload)
			throws AnalyzeModuleSAWException, JSONValidationSAWException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
