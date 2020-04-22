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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/internal/workbench/projects/")
@Api(value = "The controller provides operations to retrive streams and its contents "
	    + "synchronoss analytics platform ")
public class WorkbenchInspectController {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private RestUtil restUtil;

	private RestTemplate restTemplate = null;
	
	@Value("${workbench.rtis-stream-base}")
	@NotNull
	private String rtisBasePath;
	
	@Value("${workbench.rtis-base-url}")
	@NotNull
	private String rtisUrl;
	
	
	public static final String RTIS_CONFIG_URL =  "/internal/rtisconfig/config/";
	public static final String RTIS_APP_KEYS_URL =  "/internal/rtisconfig/appKeys";

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
	@ApiOperation(value = "Retrieves stream details such as name and topic name", nickname = "retrieveStream",
		      notes = "", response = HttpStatus.class)
		  @ApiResponses(
		      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
		          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		          @ApiResponse(code = 500, message = "Server is down. Contact System administrator")})
	@RequestMapping(value = "{project}/streams", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object retrieveStreamsDetails(HttpServletRequest req, 
			@PathVariable(name = "project", required = true) String project)
			throws JsonProcessingException, Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", req.getHeader("Authorization"));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		logger.debug("Authorization header.....####" + req.
				getHeader("Authorization"));
		
		logger.debug(" RTIS base url ::"+ this.rtisUrl);
		String rtisAppkeysUrl = this.rtisUrl + RTIS_APP_KEYS_URL;
		logger.debug("rtis app key url ::"+ rtisAppkeysUrl);
		
		ResponseEntity<Object[]> appKeys = restTemplate.
				exchange(rtisAppkeysUrl,
				HttpMethod.GET, entity, Object[].class);
		logger.debug(appKeys.toString());

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(appKeys.getBody());
		JsonNode objects = mapper.readTree(json);
		List<JsonNode> entities = new ArrayList<>();

		for (final JsonNode objNode : objects) {
			ObjectNode resultNode = mapper.createObjectNode();
			JsonNode appKey = objNode.get("app_key");
			logger.debug("########" + appKey.asText());
			
			logger.debug("#### rtis base url ::"+ this.rtisUrl);
			String rtisConfigUrl = this.rtisUrl + RTIS_CONFIG_URL;
			logger.debug("#### rtis config url ::" + rtisConfigUrl 
					+ appKey.asText());
			 
			ResponseEntity<Object[]> config = restTemplate.exchange(
				rtisConfigUrl 
			+ appKey.asText(), HttpMethod.GET, entity, Object[].class);
			logger.debug("##### config response ###" + config.toString());
			String configJson = mapper.writeValueAsString(config.getBody());

			logger.debug("#####config response ::" + configJson);
			JsonNode configObjects = mapper.readTree(configJson);
			if (configObjects.isArray()) {
				logger.debug("Is array @####");
				for (JsonNode jsonNode : configObjects) {
					JsonNode streamInfo = jsonNode.get("streams_1");
					List<String> streamsNames = new ArrayList<>();
					
					if(streamInfo.isArray()) {
						for (final JsonNode streamNode : streamInfo) {
					        logger.debug("###Inside loop ###"+ streamNode.get("queue").asText());
					        streamsNames.add(streamNode.get("queue").asText());
					    }
					}
					Optional<List<String>> eventTypes = this.retrieveEventTypes(streamsNames.get(0));
					if(eventTypes.isPresent()) {
						ArrayNode eventSNode = mapper.valueToTree(eventTypes.get());
						resultNode.putArray("eventTypes").addAll(eventSNode);
						resultNode.set("stream", jsonNode.get("streams_1"));
					}
				}
			}
			entities.add(resultNode);
		}
		return new ResponseEntity<>(entities, HttpStatus.OK);
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
	@ApiOperation(value = "Retrieves stream contents of stream", nickname = "retrieveStreamContents",
		      notes = "", response = HttpStatus.class)
    @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System administrator")})
	@RequestMapping(value = "{project}/streams/{stream}/content/{eventType}", 
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object getStreamContentByEvent(
			@PathVariable(name = "project", required = true) String project,
			@PathVariable(name = "stream", required = true) String stream,
			@PathVariable(name = "eventType", required = true) String eventType) throws IOException {
		
			logger.debug("Stream Name :::" + stream);
			List<JsonNode> entities = new ArrayList<>();
			MarlinDocumentStream docStream = this.retrieveStream(stream);
			if(docStream == null) {
				return new ResponseEntity<Object>(
					"No stream exists with name "
					+ HtmlUtils.htmlEscape(stream), HttpStatus.BAD_REQUEST);
			}
			ObjectMapper mapper = new ObjectMapper();
			docStream.forEach(document -> {
				try {
					logger.debug("########Retrieved stream content.... {}", document.asJsonString());
					JsonNode json = mapper.readTree(document.asJsonString());
					String valText = json.get("value").asText();
					logger.debug("###Value###::"+ valText);
					byte[] bytes =  Base64.getDecoder().decode(valText);
					logger.debug("###bytes length ::" + bytes.length);
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
				} catch (Exception exception) {
					logger.error(exception.getMessage(), exception);
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
	private Optional<List<String>> retrieveEventTypes(String stream) {
		   
			MarlinDocumentStream docStream = this.retrieveStream(stream);
			if(docStream == null) {
				return Optional.empty();
			}
			ObjectMapper mapper = new ObjectMapper();
			final List<String> eventTypes = new ArrayList<>();
			docStream.forEach(document -> {
				try {
					logger.debug("########Retrieved stream content.... {}", document.asJsonString());
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
					
				} catch (Exception exception) {
					logger.error(exception.getMessage(), exception);
				}
			});
		
		return Optional.of(eventTypes.stream().distinct().
				collect(Collectors.toList()));
	}
	
	private MarlinDocumentStream retrieveStream(String stream) {
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
			logger.warn(exception.getMessage(), exception);
		} catch(Exception exception) {
			logger.warn(exception.getMessage(), exception);
		}

		if(isStreamExists) {
			logger.debug("########Retrived store...." + store);
		    docStream = (MarlinDocumentStream) store.find();
			store.close();
		}
		
		return docStream;
	}
}
