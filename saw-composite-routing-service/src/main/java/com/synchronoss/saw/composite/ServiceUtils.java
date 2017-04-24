package com.synchronoss.saw.composite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Preconditions;
import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.synchronoss.saw.composite.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.composite.model.Constraints;
import com.synchronoss.saw.composite.model.Contents;
import com.synchronoss.saw.composite.model.Key;
import com.synchronoss.saw.composite.model.Links;
import com.synchronoss.saw.composite.model.RoutingPayload;
import com.synchronoss.saw.composite.model.Contents.Action;

@Component
public class ServiceUtils {
	// private static final Logger LOG =
	// LoggerFactory.getLogger(ServiceUtils.class);

	/*
	 * @Autowired private LoadBalancerClient loadBalancer;
	 */
	public final String SCHEMA_FILENAME = "payload-schema.json";

	public ObjectMapper getMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		return objectMapper;
	}

	/**
	 *
	 * @param serviceId
	 * @return
	 */
	/*
	 * public URI getServiceUrl(String serviceId) { return
	 * getServiceUrl(serviceId, null); }
	 */
	/**
	 *
	 * @param serviceId
	 * @param fallbackUri
	 * @return
	 */
	/*
	 * protected URI getServiceUrl(String serviceId, String fallbackUri) { URI
	 * uri = null; try { ServiceInstance instance =
	 * loadBalancer.choose(serviceId);
	 * 
	 * if (instance == null) { throw new
	 * RuntimeException("Can't find a service with serviceId = " + serviceId); }
	 * 
	 * uri = instance.getUri();
	 * LOG.debug("Resolved serviceId '{}' to URL '{}'.", serviceId, uri);
	 * 
	 * } catch (RuntimeException e) {
	 * 
	 * if (fallbackUri == null) { throw e;
	 * 
	 * } else { uri = URI.create(fallbackUri);
	 * LOG.warn("Failed to resolve serviceId '{}'. Fallback to URL '{}'.",
	 * serviceId, uri); } }
	 * 
	 * return uri; }
	 */
	public <T> ResponseEntity<T> createOkResponse(T body) {
		return createResponse(body, HttpStatus.OK);
	}

	/**
	 * Clone an existing result as a new one, filtering out http headers that
	 * not should be moved on and so on...
	 *
	 * @param result
	 * @param <T>
	 * @return
	 */
	public <T> ResponseEntity<T> createResponse(ResponseEntity<T> result) {

		ResponseEntity<T> response = createResponse(result.getBody(), result.getStatusCode());
		return response;
	}

	public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
		return new ResponseEntity<>(body, httpStatus);
	}

	public Resource getClassPathResources(String filename) {
		return new ClassPathResource(filename);
	}

	public Boolean jsonSchemaValidate(String jsonDataString, String filename) throws IOException, ProcessingException {
		final JsonNode data = JsonLoader.fromString(jsonDataString);
		final JsonNode schema = JsonLoader.fromURL(this.getClassPathResources(filename).getURL());
		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		JsonValidator validator = factory.getValidator();
		ProcessingReport report = validator.validate(schema, data);
		return report.isSuccess();
	}

	public String getSAWAddress(LoadBalancerClient loadBalancerClient, String serviceId) {

		ServiceInstance serviceInstance = loadBalancerClient.choose(serviceId);
		if (serviceInstance != null) {
			return String.format("http://%s:%d/%s", serviceInstance.getHost(), serviceInstance.getPort(),
					serviceInstance.getServiceId());
		} else {
			throw new IllegalStateException("Unable to locate a leaderboard service");
		}
	}

	public boolean checkContraints(String value) {
		boolean exists = false;

		try {
			Constraints.find(value);
			exists = true;
		} catch (IllegalStateException ex) {
			exists = false;
		}

		return exists;
	}

	public boolean checkKeysConstraintsForRead(RoutingPayload routingPayload) throws NullPointerException, JSONValidationSAWException
	{
		boolean correct = true;
		if (routingPayload.getContents().getAction() !=null)
		{
			if (routingPayload.getContents().getAction().value().
					equals(Action.READ.value()))
			{
				List<Key> items = routingPayload.getContents().getKeys();
				
				items.forEach(item->
					{
						if (item.getAdditionalProperties().get("id").equals("") || item.getAdditionalProperties().get("id") == null)
						{
							throw new NullPointerException("id attribute is not valid.");
						}	
					});
			}
			else 
			{
				throw new NullPointerException("action attribute is not mentioned.");			
			}
		}
		else {
			throw new JSONValidationSAWException("Request body is not aligned with the specification");
		}
		return correct;
	}
	
	
	public boolean checkKeysConstraints(RoutingPayload routingPayload, String key)
	{
		boolean correct = true;
		if (routingPayload.getContents().getAction() !=null)
		{
				List<Key> items = routingPayload.getContents().getKeys();
				
				items.forEach(item->
					{
						if (item.getAdditionalProperties().get(key).equals("") || item.getAdditionalProperties().get(key) == null)
						{
							throw new NullPointerException(key + " attribute is not valid.");
						}	
					});
				
			}
		else {
			throw new JSONValidationSAWException("Request body is not aligned with the specification");
		}
		return correct;
	}

}
