package com.synchronoss.saw.storage.proxy.service;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Action;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.ResultFormat;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;
import com.synchronoss.saw.storage.proxy.model.response.CountESResponse;
import com.synchronoss.saw.storage.proxy.model.response.CreateAndDeleteESResponse;
import com.synchronoss.saw.storage.proxy.model.response.Hit;
import com.synchronoss.saw.storage.proxy.model.response.SearchESResponse;

@Service
public class StorageProxyServiceImpl implements StorageProxyService {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyServiceImpl.class);
  
  @Value("${schema.file}")
  private String schemaFile;
  
  private String dateFormat="yyyy-mm-dd hh:mm:ss";
  private String QUERY_REG_EX = ".*?(size|from).*?(\\d+).*?(from|size).*?(\\d+)";
  
  @Autowired
  private StorageConnectorService storageConnectorService;

  @Override
  public StorageProxyResponse execute(StorageProxyNode proxy) throws Exception {
    logger.debug("Executing data storage proxy for {}", proxy.getProxy().size()); 
    logger.trace("Validating Schema is started"); 
    Boolean validate = StorageProxyUtils.jsonSchemaValidate(proxy, schemaFile);
    logger.trace("Validating Schema is finished"); 
    StorageProxyResponse response =null;
    if (validate)
    {
      for (StorageProxy storageProxy: proxy.getProxy())
       {
        String storageType = storageProxy.getStorage().value();
        switch (storageType){
          case "ES" :  
            String action = storageProxy.getAction().value();         
            if (action.equals(Action.CREATE.value()) || action.equals(Action.DELETE.value()) 
                || action.equals(Action.SNCRPIVOT.value()) || action.equals(Action.COUNT.value()) || action.equals(Action.SEARCH.value())){
                        
                        
                          if (action.equals(Action.CREATE.value()) || action.equals(Action.DELETE.value()) || action.equals(Action.COUNT.value())){
                                Preconditions.checkArgument(!(storageProxy.getResultFormat().value().equals(ResultFormat.TABULAR.value())), "The result format for above operations cannot be in tabular format");
                                storageProxy.setResultFormat(ResultFormat.JSON);
                                switch (action) {
                                  case "create" : 
                                    CreateAndDeleteESResponse createResponse =(CreateAndDeleteESResponse) storageConnectorService.createDocument(storageProxy.getQuery(), storageProxy);
                                                storageProxy.setStatusMessage("created with an id :" + createResponse.getId());
                                                List<Object> indexData = new ArrayList<>();
                                                indexData.add(createResponse);
                                                storageProxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                storageProxy.setData(indexData);
                                                break;
                                  case "count" : 
                                                CountESResponse countResponse =(CountESResponse) storageConnectorService.countDocument(storageProxy.getQuery(), storageProxy);
                                                storageProxy.setStatusMessage("Total number of documents are :" + countResponse.getCount());
                                                List<Object> countData = new ArrayList<>();
                                                countData.add(countResponse);
                                                storageProxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                storageProxy.setData(countData);
                                                break;
                                  case "delete" : 
                                                CreateAndDeleteESResponse deleteResponse =(CreateAndDeleteESResponse) storageConnectorService.deleteDocumentById(storageProxy.getEntityId(), storageProxy);
                                                storageProxy.setStatusMessage("deleted with an id :" + deleteResponse.getId());
                                                List<Object> deleteData = new ArrayList<>();
                                                deleteData.add(deleteResponse);
                                                storageProxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                storageProxy.setData(deleteData);
                                                break;
                                } 
                          }// Action only to support JSON format
                          else {
                            switch (action){
                              case "sncrpivot" : 
                                               if (storageProxy.getSqlBuilder()!=null){
                                                 Preconditions.checkArgument(storageProxy.getQuery()!=null, "Query cannot be null.");  
                                                 String query = storageProxy.getQuery();
                                                 int size =0;
                                                   storageProxy.setPageSize(0);
                                                   storageProxy.setPageNum(0);
                                                   if(storageProxy.getQuery().contains("size") && !query.contains("from")){
                                                     Pattern p = Pattern.compile(QUERY_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                                                     Matcher m = p.matcher(query);
                                                     if (m.find())
                                                     {
                                                         String fromSize_1=m.group(1).trim();
                                                         String fromSize_1_Num=m.group(2);
                                                         String fromSize_2_Num=m.group(4);
                                                         if (fromSize_1.equals("size")){
                                                           size = Integer.parseInt(fromSize_1_Num);
                                                           }
                                                         else{
                                                           size = Integer.parseInt(fromSize_2_Num);
                                                         }
                                                     } // parsing of size & from
                                                   if (size ==0){ 
                                                   SearchESResponse<?> sncrPivotResponse =(SearchESResponse<?>) storageConnectorService.searchDocuments(storageProxy.getQuery(), storageProxy);
                                                   if (storageProxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                                       logger.debug("Data from Aggregation :" +sncrPivotResponse.getAggregations());
                                                           ObjectMapper mapperObj = new ObjectMapper();
                                                           String jsonString = mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                                                           JsonNode objectNode = mapperObj.readTree("{ \"data\":"+jsonString + "}");
                                                           PivotSNCRFlattener pivotSNCRFlattener = new PivotSNCRFlattener(storageProxy.getSqlBuilder());
                                                           List<Object> flatData= pivotSNCRFlattener.parseData(objectNode);
                                                           storageProxy.setData(flatData);
                                                           storageProxy.setStatusMessage("Data has been retrieved & has been flattened.");
                                                        logger.debug("Data from Aggregation converted into Flat Data " +flatData);
                                                   }else{
                                                     logger.debug("Data from Aggregation :" +sncrPivotResponse.getAggregations());
                                                       ObjectMapper mapperObj = new ObjectMapper();
                                                       String jsonString = mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                                                       JsonNode objectNode = mapperObj.readTree("{ \"data\":"+jsonString + "}");
                                                       PivotSNCRFlattener pivotSNCRFlattener = new PivotSNCRFlattener(storageProxy.getSqlBuilder());
                                                       List<Map<String, Object>> flatData= pivotSNCRFlattener.parseDataMap(objectNode);
                                                       List<Object> data = new ArrayList<>();
                                                       List<Object> tabularData = StorageProxyUtils.getTabularFormat(flatData, StorageProxyUtils.COMMA); 
                                                       for (Object obj : tabularData){
                                                         data.add(obj);
                                                       }
                                                       storageProxy.setData(data);
                                                       storageProxy.setStatusMessage("Data has been retrieved & has been flattened.");
                                                     logger.debug("Data from Aggregation into JSON Format " +sncrPivotResponse.getAggregations()); 
                                                       }
                                                   }
                                                  else {
                                                    storageProxy.setStatusMessage("size cannot be greater or less than zero.");
                                                  }
                                                  }
                                                  else {
                                                    storageProxy.setStatusMessage("Please provide size with value 'size=0' & it shall not conti 'from' parameter in query.");
                                                    storageProxy.setPageSize(0);
                                                    storageProxy.setPageNum(0);
                                                  }
                                                } else {
                                                  storageProxy.setStatusMessage("To process the action type of sncrpivot, sqlBuilder is mandatory");
                                                }
                                               break;
                              case "search" : 
                                              Preconditions.checkArgument(storageProxy.getQuery()!=null, "Query cannot be null.");
                                              String query = storageProxy.getQuery();
                                              if(query.contains("size") && query.contains("from")){
                                                Pattern p = Pattern.compile(QUERY_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                                                Matcher m = p.matcher(query);
                                                if (m.find())
                                                {
                                                    String fromSize_1=m.group(1).trim();
                                                    String fromSize_1_Num=m.group(2);
                                                    String fromSize_2_Num=m.group(4);
                                                    if (fromSize_1.equals("size")){
                                                      storageProxy.setPageSize(Integer.parseInt(fromSize_1_Num));
                                                      storageProxy.setPageNum(Integer.parseInt(fromSize_2_Num)); 
                                                      }
                                                    else{
                                                      storageProxy.setPageSize(Integer.parseInt(fromSize_2_Num));
                                                      storageProxy.setPageNum(Integer.parseInt(fromSize_1_Num)); 
                                                    }
                                                } // parsing of size & from
                                              if(storageProxy.getPageSize()<= 50000){ 
                                              SearchESResponse<?> searchResponse =(SearchESResponse<?>) storageConnectorService.searchDocuments(storageProxy.getQuery(), storageProxy);
                                              long actualCount = searchResponse.getHits()!=null? searchResponse.getHits().getTotal() : 0;
                                              if (actualCount >0){
                                              storageProxy.setStatusMessage("Number of documents found for provided query :" + actualCount);
                                              List<Hit<?>> hits = searchResponse.getHits().getHits();
                                              List<Object> data = new ArrayList<>();
                                              if (storageProxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                                 for (Hit<?> hit : hits){
                                                 data.add(hit.getSource());
                                                }
                                                storageProxy.setData(data);
                                              } // this block only for JSON format
                                              else{
                                                List<Map<String,Object>> dataHits = new ArrayList<>();
                                                for (Hit<?> hit : hits){
                                                  dataHits.add(hit.getSource());
                                                 }
                                               List<Object> tabularData = StorageProxyUtils.getTabularFormat(dataHits, StorageProxyUtils.COMMA); 
                                               for (Object obj : tabularData){
                                                 data.add(obj);
                                               }   
                                              } // this block only for Tabular format
                                              storageProxy.setData(data);
                                              }
                                              else {
                                                storageProxy.setStatusMessage("There are no documents available with the provided");
                                              }
                                              } // end of check for the size 50000
                                              else {
                                                storageProxy.setStatusMessage("The size cannot be greater than 50000");
                                              }
                                              }
                                              else{
                                                storageProxy.setStatusMessage("Please provide size & from parameter in query.");
                                                storageProxy.setPageSize(0);
                                                storageProxy.setPageNum(0);
                                              }
                                              break;
                                          }
                            // TODO: Execute Query or perform 'search' & 'sncrpivot' prepare response based on the specification (either JSON or Tabular) 
                            // TODO: Convert data either into tabular or JSON
                          }
            } // end of action operation  if block
            else {
              storageProxy.setStatusMessage("This "+action+" is not yet supported by StorageType :" + storageType);
            }
              break;
              
          case "DL" :  storageProxy.setStatusMessage("Not supported. This feature is yet to be implemented");break;
              // Below are the steps for future implementation to support other storage implementation
              // a)
              // TODO: Storage Type : DL
              // TODO: Validate Spark Query (executing API & prepare the response with for validation using grammar file)
              //       (https://github.com/apache/spark/blob/master/sql/catalyst/src/main/antlr4/org/apache/spark/sql/catalyst/parser/SqlBase.g4)
              // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
              // TODO: Convert data either into tabular or JSON
          
          case  "RDMS": storageProxy.setStatusMessage("Not supported. This feature is yet to be implemented");break;
              // TODO: Storage Type : RDBMS (MYSQL/Oracle)
              // b)
              // TODO: Validate PL/SQL Query (executing API & prepare the response with for validation using grammar file)
              //       (https://github.com/antlr/grammars-v4/tree/master/mysql)
              //       (https://github.com/antlr/grammars-v4/tree/master/plsql)
              // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
              // TODO: Convert data either into tabular or JSON
          
        } // end of switch statement
        } // end of for loop
    response = StorageProxyUtils.prepareResponse(proxy.getProxy(), "data is retrieved");
    } // end of schema validation if block
    else {
      response = StorageProxyUtils.prepareResponse(proxy.getProxy(), "provided JSON input is not valid.");
    }
    return response;
  }
  
}
