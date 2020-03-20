package com.synchronoss.saw.workbench.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mapr.streams.Streams;
import com.mapr.streams.impl.MarlinDocumentStream;
import com.mapr.streams.impl.MessageStore;
import com.synchronoss.sip.utils.RestUtil;

@RestController
@RequestMapping("/internal/workbench/projects/")
public class WorkbenchInspectController {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Autowired
	private RestUtil restUtil;

	private RestTemplate restTemplate = null;

	@PostConstruct
	public void init() {
		restTemplate = restUtil.restTemplate();
	}

	@RequestMapping(value = "{project}/streams", method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object getAppKeys(HttpServletRequest req, 
			@PathVariable(name = "project", required = true) String project)
			throws JsonProcessingException, Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", req.getHeader("Authorization"));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		logger.debug("Authroization header.....####" + req.
				getHeader("Authorization"));
		ResponseEntity<Object[]> appKeys = restTemplate.
				exchange("http://localhost:9501/internal/rtisconfig/appKeys",
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
					"http://localhost:9501/internal/rtisconfig/config/" 
			+ appKey.asText(), HttpMethod.GET, entity,
					Object[].class, new Object[0]);
			logger.debug("##### config response ###" + config.toString());
			String configJson = mapper.writeValueAsString(config.getBody());
			logger.debug("#####config response ::" + configJson);
			JsonNode configObjects = mapper.readTree(configJson);
			if (configObjects.isArray()) {
				logger.debug("Is array @####");
				resultNode.put("key:", appKey.asText());
				for (JsonNode jsonNode : configObjects) {
					resultNode.put("streams_1:", jsonNode.get("streams_1"));
					resultNode.put("streams_2:", jsonNode.get("streams_2"));
				}
			}
			entities.add(resultNode);

		}

		return new ResponseEntity<List<JsonNode>>(entities, HttpStatus.OK);
	}

	@RequestMapping(value = "{project}/streams/{stream}/content", 
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Object getStreamContent(
			@PathVariable(name = "project", required = true) String project,
			@PathVariable(name = "stream", required = true) String stream) {
		logger.debug("Stream Name :::" + stream);
		List<JsonNode> entities = new ArrayList<JsonNode>();
		
			MessageStore store = null;
			try {
				store = (MessageStore) Streams.getMessageStore("/var/sip/streams/stream_1");
			} catch (IOException exception) {
				logger.error(exception.getMessage());
			}
			
			
			System.out.print("########Retrived store...." + store);
			MarlinDocumentStream docStream = (MarlinDocumentStream) store.find();
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
					JsonNode value = jsonObj.get("payload");
					logger.debug("####Entire payload ::"+ value);
					String data = new String(Base64.getDecoder().decode(value.asText()));
					logger.debug("####Final ###"+ data);
					entities.add(mapper.readTree(data));
					
			} catch (IOException exception) {
				logger.error(exception.getMessage());
			}	catch (Exception exception) {
				logger.error(exception.getMessage());
			}
						
			});
			store.close();

		
		return entities;

	}
}
