package com.synchronoss.saw.storage.proxy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.ValidationException;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Action;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.ResultFormat;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;

@Service
public class StorageProxyServiceImpl implements StorageProxyService {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyServiceImpl.class);
  
  @Value("${schema.file}")
  private String schemaFile;
  
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
            if (action.equals(Action.CREATE.value()) || action.equals(Action.UPDATE.value()) || action.equals(Action.DELETE.value()) 
                || action.equals(Action.SNCRPIVOT.value()) || action.equals(Action.COUNT.value()) || action.equals(Action.SEARCH.value())){
                        ValidateQueryResponse validateResponse= (ValidateQueryResponse) storageConnectorService.validateQuery(storageProxy.getQuery(), storageProxy);
                        if (validateResponse.isValid()){
                          if (action.equals(Action.CREATE.value()) || action.equals(Action.UPDATE.value()) || action.equals(Action.DELETE.value()) 
                               || action.equals(Action.COUNT.value())){
                                Preconditions.checkArgument(storageProxy.getResultFormat().value().equals(ResultFormat.TABULAR.value()), "The result format for above operations cannot be in tabular format");
                                storageProxy.setResultFormat(ResultFormat.JSON);
                                switch (action) {
                                  case "create" : 
                                                IndexResponse indexResponse =(IndexResponse) storageConnectorService.indexDocument(storageProxy.getQuery(), storageProxy);
                                                storageProxy.setStatusMessage("created with an id :" + indexResponse.getId());
                                                List<Object> indexData = new ArrayList<>();
                                                indexData.add(indexResponse.getShardInfo().toString());
                                                storageProxy.setData(indexData);
                                                break;
                                  case "update" : 
                                                UpdateResponse updateResponse =(UpdateResponse) storageConnectorService.updateDocument(storageProxy.getEntityId(), storageProxy);
                                                storageProxy.setStatusMessage("updated with an id :" + updateResponse.getId());
                                                List<Object> updateData = new ArrayList<>();
                                                updateData.add(updateResponse.getShardInfo().toString());
                                                storageProxy.setData(updateData);
                                                break;
                                  case "count" : 
                                                Long countResponse =(Long) storageConnectorService.countDocument(storageProxy.getQuery(), storageProxy);
                                                storageProxy.setStatusMessage("Total number of documents are :" + countResponse.longValue());
                                                List<Object> countData = new ArrayList<>();
                                                countData.add("{ \"count\":" + countResponse.longValue() +"}");
                                                storageProxy.setData(countData);
                                                break;
                                  case "delete" : 
                                                DeleteResponse deleteResponse =(DeleteResponse) storageConnectorService.deleteDocument(storageProxy.getEntityId(), storageProxy);
                                                storageProxy.setStatusMessage("deleted with an id :" + deleteResponse.getId());
                                                List<Object> deleteData = new ArrayList<>();
                                                deleteData.add(deleteResponse.getShardInfo().toString());
                                                storageProxy.setData(deleteData);
                                                break;
                                } 
                          }// Action only to support JSON format
                          else {
                            switch (action){
                              case "sncrpivot" : 
                                               Preconditions.checkArgument(storageProxy.getSqlBuilder()!=null, "To process action type sncrpivot, sqlBuilder is mandatory");     
                                               storageProxy.setPageSize(0);
                                               storageProxy.setPageNum(0);
                                               SearchResponse sncrPivotResponse =(SearchResponse) storageConnectorService.searchDocuments(storageProxy.getQuery(), storageProxy);
                                               logger.debug("Data from Aggregation" +sncrPivotResponse.getAggregations().toString());
                                               break;
                              case "search" : 
                                              SearchResponse searchResponse =(SearchResponse) storageConnectorService.searchDocuments(storageProxy.getQuery(), storageProxy);
                                              long actualCount = searchResponse.getHits()!=null? searchResponse.getHits().getTotalHits() : 0;
                                              if (actualCount >0){
                                              storageProxy.setStatusMessage("Number of documents found for provided query :" + actualCount);
                                              SearchHit [] hits = searchResponse.getHits().getHits();
                                              List<Object> data = new ArrayList<>();
                                              if (storageProxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                                 for (SearchHit hit : hits){
                                                 data.add(hit.getSource());
                                                }
                                                storageProxy.setData(data);
                                              } // this block only for JSON format
                                              else{
                                                List<Map<String,Object>> dataHits = new ArrayList<>();
                                                for (SearchHit hit : hits){
                                                  dataHits.add(hit.getSource());
                                                 }
                                               data.add(StorageProxyUtils.getTabularFormat(dataHits, StorageProxyUtils.COMMA)); 
                                              } // this block only for Tabular format
                                              storageProxy.setData(data);
                                              }
                                              else {
                                                storageProxy.setStatusMessage("There are no documents available with the provided ");
                                              }
                                              break;
                                          }
                            // TODO: Execute Query or perform 'search' & 'sncrpivot' prepare response based on the specification (either JSON or Tabular) 
                            // TODO: Convert data either into tabular or JSON
                          }
                        } // end of if block for validation of query which needs to be executed
                        else {
                          storageProxy.setStatusMessage("Provided Query is not valid due to : " +validateResponse.getQueryExplanation().toString());
                          throw new ValidationException();
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
