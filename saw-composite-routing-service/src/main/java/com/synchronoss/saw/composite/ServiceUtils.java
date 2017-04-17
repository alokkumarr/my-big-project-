package com.synchronoss.saw.composite;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;


@Component
public class ServiceUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceUtils.class);

    @Autowired
    private LoadBalancerClient loadBalancer;
    
    public final String SCHEMA_FILENAME = "payload-schema.json";

    /**
     *
     * @param serviceId
     * @return
     */
    public URI getServiceUrl(String serviceId) {
        return getServiceUrl(serviceId, null);
    }

    /**
     *
     * @param serviceId
     * @param fallbackUri
     * @return
     */
    protected URI getServiceUrl(String serviceId, String fallbackUri) {
        URI uri = null;
        try {
            ServiceInstance instance = loadBalancer.choose(serviceId);

            if (instance == null) {
                throw new RuntimeException("Can't find a service with serviceId = " + serviceId);
            }

            uri = instance.getUri();
            LOG.debug("Resolved serviceId '{}' to URL '{}'.", serviceId, uri);

        } catch (RuntimeException e) {
      
            if (fallbackUri == null) {
                throw e;

            } else {
                uri = URI.create(fallbackUri);
                LOG.warn("Failed to resolve serviceId '{}'. Fallback to URL '{}'.", serviceId, uri);
            }
        }

        return uri;
    }

    public <T> ResponseEntity<T> createOkResponse(T body) {
        return createResponse(body, HttpStatus.OK);
    }

    /**
     * Clone an existing result as a new one, filtering out http headers that not should be moved on and so on...
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
    
    public Resource getClassPathResources (String filename){
		return new ClassPathResource(filename);
    }
   
    public Boolean jsonSchemaValidate (String jsonDataString, String filename) throws IOException, ProcessingException
    {
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
            return String.format("http://%s:%d/%s", serviceInstance.getHost(), serviceInstance.getPort(),serviceInstance.getServiceId());
        } else {
            throw new IllegalStateException("Unable to locate a leaderboard service");
        }
    }

}
