package com.synchronoss.saw.storage.proxy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.storage.proxy.AsyncConfiguration;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.Content;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Action;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.ResultFormat;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Storage;
import com.synchronoss.saw.storage.proxy.model.StorageProxyRequestBody;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;

/**
 * @author spau0004
 * This class is used to perform CRUD operation by storage
 * The requests are JSON documents in the following formats
 * "{
 "contents": {
   "proxy" : [
  {
    "storage" : "ES",
    "action" : "search",
    "query" : "",
    "requestBy" :"admin@sycnchrnoss.com",
    "objectType" : "",
    "indexName": "",
    "tableName": "",
    "objectName":"",
    "requestedTime":"",
    "productCode": "",
    "moduleName":"",
    "dataSecurityKey":[],
    "resultFormat":"",
    "data": []
}
]
}
}"
 */
@RestController
public class StorageProxyController {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyController.class);

  @Autowired
  private StorageProxyService proxyService;
  
  /**
   * This method is used to get the data based on the storage type<br/>
   * perform conversion based on the specification asynchronously
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @Async(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
  @RequestMapping(value = "/internal/proxy/storage/async", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public CompletableFuture<StorageProxy> retrieveStorageDataAsync(@RequestBody StorageProxy requestBody) {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    CompletableFuture<StorageProxy> responseObjectFuture = null;
   try {
     ObjectMapper objectMapper = new ObjectMapper();
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
     objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
     //StorageProxyNode proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody), "contents");
     logger.trace("Storage Proxy async request object : {} ", objectMapper.writeValueAsString(requestBody));
     responseObjectFuture= CompletableFuture.
         supplyAsync(() -> {
          StorageProxy proxyResponseData = null; 
            try {
              proxyResponseData = proxyService.execute(requestBody);
            } catch (IOException e) {
              logger.error("While retrieving data there is an exception.", e);
              proxyResponseData= StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
            } catch (ProcessingException e) {
              logger.error("Exception generated while validating incoming json against schema.", e);
              proxyResponseData= StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
            }catch (Exception e) {
              logger.error("Exception generated while processing incoming json.", e);
              proxyResponseData= StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
            }
        return proxyResponseData;
         })
         .handle((res, ex) -> {
           if(ex != null) {
               logger.error("While retrieving data there is an exception.", ex);
               res.setStatusMessage(ex.getCause().toString());
               return res;
           }
           return res;
       });
    } catch (IOException e) {
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } 
   return responseObjectFuture;
  }

  /**
   * This method is used to get the data based on the storage type<br/>
   * perform conversion based on the specification asynchronously
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   * @throws JsonProcessingException 
   */
  
  @RequestMapping(value = "/internal/proxy/storage/", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  public StorageProxy retrieveStorageDataSync(@RequestBody StorageProxy requestBody) throws JsonProcessingException {
    logger.debug("Request Body:{}", requestBody);
    if (requestBody == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    StorageProxy responseObjectFuture = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    try {
     //proxyNode = StorageProxyUtils.getProxyNode(objectMapper.writeValueAsString(requestBody), "contents");
     logger.trace("Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(requestBody));
     responseObjectFuture= proxyService.execute(requestBody);
    } catch (IOException e){
      logger.error("expected missing on the request body.", e);
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex){
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (ProcessingException e){
      logger.error("Exception generated while validating incoming json against schema.", e);
      responseObjectFuture= StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
    }catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      responseObjectFuture= StorageProxyUtils.prepareResponse(requestBody, e.getCause().toString());
    }
   logger.trace("response data {}", objectMapper.writeValueAsString(responseObjectFuture));
   return responseObjectFuture;
  }  
 
  
  public static void main(String[] args) throws JsonProcessingException {
    
    StorageProxy proxy = new StorageProxy();
    proxy.setAction(Action.SEARCH);
    proxy.setStorage(Storage.ES);
    proxy.setObjectType("HSI");
    proxy.setIndexName("tmo");
    proxy.setRequestBy("saurav.paul@sycnhronoss.com");
    proxy.setRequestedTime(new Date().toString());
    proxy.setResultFormat(ResultFormat.JSON);
    proxy.setProductCode("MCT");
    proxy.setModuleName("SAW");
    Content content = new Content();
    List<StorageProxy> proxies = new ArrayList<>();
    proxies.add(proxy);
    content.setProxy(proxies);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    StorageProxyRequestBody requestBody = new StorageProxyRequestBody();
    requestBody.setContent(content);
    System.out.println(objectMapper.writeValueAsString(requestBody));

    
  }
}
  
  
