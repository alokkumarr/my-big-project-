package com.synchronoss.saw.workbench.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.apache.kafka.common.requests.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mapr.streams.Streams;
import com.mapr.streams.impl.MarlinDocumentStream;
import com.mapr.streams.impl.MessageStore;
import com.synchronoss.sip.utils.RestUtil;

import akka.japi.Option;

@RestController
@RequestMapping("/internal/workbench/projects/")
public class WorkbenchInspectController {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private RestUtil restUtil;

	private RestTemplate restTemplate = null;
	
	@Value("${workbench.rtis-stream-base}")
	@NotNull
	private String rtisBasePath;
	
	@Value("${workbench.rtis-appkeys-url}")
	@NotNull
	private String rtisAppkeysUrl;
	
	@Value("${workbench.rtis-config-url}")
	@NotNull
	private String rtisConfigUrl;
	
	

	  
	@PostConstruct
	public void init() {
		restTemplate = restUtil.restTemplate();
	}
	
	/**
	 * Retrieves  streams and their configuration
	 * from current Kafka queue.
	 * 
	 * 
	 * @param req request object
	 * @param project projId
	 * @return streams details
	 * @throws JsonProcessingException exception while parsing json
	 * @throws Exception exception
	 */
	@RequestMapping(value = "{project}/streams", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object retrieveStreamsDetails(HttpServletRequest req, 
			@PathVariable(name = "project", required = true) String project)
			throws JsonProcessingException, Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", req.getHeader("Authorization"));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		logger.debug("Authroization header.....####" + req.
				getHeader("Authorization"));
		ResponseEntity<Object[]> appKeys = restTemplate.
				exchange(this.rtisAppkeysUrl,
				HttpMethod.GET, entity, Object[].class, new Object[0]);
		logger.debug(appKeys.toString());

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(appKeys.getBody());
		JsonNode objects = mapper.readTree(json);
		List<JsonNode> entities = new ArrayList<JsonNode>();

		for (final JsonNode objNode : objects) {
			ObjectNode resultNode = mapper.createObjectNode();
			JsonNode appKey = objNode.get("app_key");
			logger.debug("########" + appKey.asText());

			ResponseEntity<Object[]> config = restTemplate.exchange(
					this.rtisConfigUrl 
			+ appKey.asText(), HttpMethod.GET, entity,
					Object[].class, new Object[0]);
			logger.debug("##### config response ###" + config.toString());
			String configJson = mapper.writeValueAsString(config.getBody());
			logger.debug("#####config response ::" + configJson);
			JsonNode configObjects = mapper.readTree(configJson);
			if (configObjects.isArray()) {	
				logger.debug("Is array @####");
				for (JsonNode jsonNode : configObjects) {
					
					//resultNode.put("streams_2:", jsonNode.get("streams_2"));
					
					JsonNode streamInfo = jsonNode.get("streams_1");
					List<String> streamsNames = new ArrayList<String>();
					
					if(streamInfo.isArray()) {
						for (final JsonNode streamNode : streamInfo) {
					        logger.debug("###Inside loop ###"+ streamNode.get("queue").asText());
					        streamsNames.add(streamNode.get("queue").asText());
					    }
					}
					
					
					
					Optional<List<String>> eventTypes = this.retriveEventTypes(streamsNames.get(0));
					if(eventTypes.isPresent()) {
						ArrayNode eventSNode = mapper.valueToTree(eventTypes.get());
						resultNode.putArray("eventTypes").addAll(eventSNode);
						resultNode.set("stream", jsonNode.get("streams_1"));
					} 
					
					
					
					
				}
			}
			entities.add(resultNode);

		}

		return new ResponseEntity<List<JsonNode>>(entities, HttpStatus.OK);
	}
	
	/**
	 * Retrieve content from stream.
	 * 
	 * @param project project id
	 * @param stream stream name
	 * @param eventType event type
	 * @return stream content
	 * @throws IOException 
	 */
	@RequestMapping(value = "{project}/streams/{stream}/content/{eventType}", 
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object getStreamContentByEvent(
			@PathVariable(name = "project", required = true) String project,
			@PathVariable(name = "stream", required = true) String stream,
			@PathVariable(name = "eventType", required = true) String eventType) throws IOException {
		
			logger.debug("Stream Name :::" + stream);
			List<JsonNode> entities = new ArrayList<JsonNode>();
			MarlinDocumentStream docStream = this.retriveStream(stream);
			if(docStream == null) {
					    return new ResponseEntity<Object>
					    ("No stream exists with name "+ stream, HttpStatus.BAD_REQUEST);
			}
			ObjectMapper mapper = new ObjectMapper();
			docStream.forEach(document -> {
				try {
				System.out.print("########Retrived stream content...." + document.asJsonString());
					JsonNode json = mapper.readTree(document.asJsonString());
					String valText = json.get("value").asText();
					logger.debug("###Value###::"+ valText);
					byte[] bytes =  Base64.getDecoder().decode(valText);
					logger.debug("###bytes legnth ::" + bytes.length);
					String content = new String(bytes);
					
					
					JsonNode jsonObj = mapper.readTree(content);
					logger.debug("####Entire JSON ::"+ jsonObj);
					if(jsonObj.get("EVENT_TYPE").
							asText().equals(eventType)) {
						
						JsonNode value = jsonObj.get("payload");
						logger.debug("####Entire payload ::"+ value);
						String data = new String(Base64.getDecoder().decode(value.asText()));
						logger.debug("####Final ###"+ data);
						entities.add(mapper.readTree(data));
					}
					
					
					
			} catch (IOException exception) {
				logger.error(exception.getMessage());
			}	catch (Exception exception) {
				logger.error(exception.getMessage());
			}
						
			});

		
		return entities;

	}
	
	
	/**
	 * Retrieves unique event types from content.
	 * 
	 * @param stream stream name
	 * @return list of event types
	 */
	private Optional<List<String>> retriveEventTypes(String stream) {
		   
			MarlinDocumentStream docStream = this.retriveStream(stream);
			if(docStream == null) {
				return Optional.empty();
			}
			ObjectMapper mapper = new ObjectMapper();
			final List<String> eventTypes = new ArrayList<String>();
			docStream.forEach(document -> {
				try {
				System.out.print("########Retrived stream content...." + document.asJsonString());
					JsonNode json = mapper.readTree(document.asJsonString());
					String valText = json.get("value").asText();
					logger.debug("###Value###::"+ valText);
					byte[] bytes =  Base64.getDecoder().decode(valText);
					logger.debug("###bytes legnth ::" + bytes.length);
					String content = new String(bytes);
					
					
					JsonNode jsonObj = mapper.readTree(content);
					logger.debug("####Entire JSON ::"+ jsonObj);
					JsonNode value = jsonObj.get("EVENT_TYPE");
					logger.debug("####Event Type ::"+ value);
					eventTypes.add(value.asText());
					
			} catch (com.mapr.db.exceptions.TableNotFoundException exception) {
				logger.error(exception.getMessage());
			}	catch (Exception exception) {
				logger.error(exception.getMessage());
			}
						
			});
		
		return Optional.of(eventTypes.stream().distinct().
				collect(Collectors.toList()));

	
		
	}
	
	private MarlinDocumentStream retriveStream(String stream) {

		logger.debug("Stream Name :::" + stream);

		logger.debug("#####stream name:::" + stream);

		MessageStore store = null;
		MarlinDocumentStream docStream = null;
		boolean isStreamExists = false ;
		try {
			
			store = (MessageStore) Streams.getMessageStore(rtisBasePath + "/" + stream);
			docStream = (MarlinDocumentStream) store.find();
			isStreamExists = true;
		} catch (com.mapr.db.exceptions.TableNotFoundException exception) {
			logger.info("#### no stream exists ####");
			logger.warn(exception.getMessage());
		}catch(IOException exception) {
			logger.warn(exception.getMessage());
		}catch(java.lang.IllegalArgumentException exception) {
			logger.warn(exception.getMessage());
		}catch(Exception exception) {
			logger.warn(exception.getMessage());
		}
		
		if(isStreamExists) {
			logger.debug("########Retrived store...." + store);
		    docStream = (MarlinDocumentStream) store.find();
			store.close();
		}

		
		return docStream;

	}
}
