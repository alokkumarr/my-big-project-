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
import com.synchronoss.saw.storage.proxy.model.StoreField;
import com.synchronoss.saw.storage.proxy.model.response.ClusterIndexResponse;
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
  private String SIZE_REG_EX = ".*?(size).*?(\\d+)";
  
  @Autowired
  private StorageConnectorService storageConnectorService;
  
  @Autowired
  private StorageProxyMetaDataService storageProxyMetaDataService;
  

  private int size;

  @Override
  public StorageProxy execute(StorageProxy proxy) throws Exception {
    logger.trace("Validating Schema is started"); 
    Boolean validate = StorageProxyUtils.jsonSchemaValidate(proxy, schemaFile);
    logger.trace("Validating Schema is finished"); 
    StorageProxy response =null;
    if (validate)
    {
      String storageType = proxy.getStorage().value();
        switch (storageType){
          case "ES" :  
            String action = proxy.getAction().value();         
            if (action.equals(Action.CREATE.value()) || action.equals(Action.DELETE.value()) 
                || action.equals(Action.PIVOT.value()) || action.equals(Action.COUNT.value()) || action.equals(Action.SEARCH.value()) || action.equals(Action.AGGREGATE.value())
                || action.equals(Action.CAT.value()) || action.equals(Action.MAPPING.value())){
                        
                        
                          if (action.equals(Action.CREATE.value()) || action.equals(Action.DELETE.value()) || action.equals(Action.COUNT.value()) || action.equals(Action.CAT.value()) || action.equals(Action.MAPPING.value())){
                                Preconditions.checkArgument(!(proxy.getResultFormat().value().equals(ResultFormat.TABULAR.value())), "The result format for above operations cannot be in tabular format");
                                proxy.setResultFormat(ResultFormat.JSON);
                                switch (action) {
                                  case "create" : 
                                    CreateAndDeleteESResponse createResponse =(CreateAndDeleteESResponse) storageConnectorService.createDocument(proxy.getQuery(), proxy);
                                                proxy.setStatusMessage("created with an id :" + createResponse.getId());
                                                List<Object> indexData = new ArrayList<>();
                                                indexData.add(createResponse);
                                                proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                proxy.setData(indexData);
                                                break;
                                  case "count" : 
                                                CountESResponse countResponse =(CountESResponse) storageConnectorService.countDocument(proxy.getQuery(), proxy);
                                                proxy.setStatusMessage("Total number of documents are :" + countResponse.getCount());
                                                List<Object> countData = new ArrayList<>();
                                                countData.add(countResponse);
                                                proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                proxy.setData(countData);
                                                break;
                                  case "delete" : 
                                                CreateAndDeleteESResponse deleteResponse =(CreateAndDeleteESResponse) storageConnectorService.deleteDocumentById(proxy.getEntityId(), proxy);
                                                proxy.setStatusMessage("deleted with an id :" + deleteResponse.getId());
                                                List<Object> deleteData = new ArrayList<>();
                                                deleteData.add(deleteResponse);
                                                proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                                proxy.setData(deleteData);
                                                break;
                                  case "_cat" : 
                                              List<ClusterIndexResponse> clusterIndexResponses = storageConnectorService.catClusterIndices(proxy);
                                              List<Object> listOfIndices = new ArrayList<>();
                                              for(ClusterIndexResponse clusterIndexResponse : clusterIndexResponses) {
                                                listOfIndices.add(clusterIndexResponse.getIndex());
                                              }
                                              proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                              proxy.setData(listOfIndices);
                                              break;
                                              
                                case "_mappings" : 
                                             List<StoreField> mappingIndexResponse = storageConnectorService.getMappingbyIndex(proxy);
                                             List<Object> mappinglist = new ArrayList<>();
                                             for(StoreField storeField : mappingIndexResponse) {
                                               mappinglist.add(storeField);
                                             }
                                             mappinglist.add(mappingIndexResponse);
                                             proxy.setResponseTime(new SimpleDateFormat(dateFormat).format(new Date()));
                                             proxy.setData(mappinglist);
                                             break;
                                } 
                          }// Action only to support JSON format
                          else {
                            switch (action){
                              case "pivot" : 
                                               if (proxy.getSqlBuilder()!=null){
                                                 Preconditions.checkArgument(proxy.getQuery()!=null, "Query cannot be null.");  
                                                 String query = proxy.getQuery();
                                                 int size =0;
                                                   proxy.setPageSize(0);
                                                   proxy.setPageNum(0);
                                                   if(proxy.getQuery().contains("size") && !query.contains("from")){
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
                                                   SearchESResponse<?> sncrPivotResponse =(SearchESResponse<?>) storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                                                   if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                                       logger.debug("Data from Aggregation :" +sncrPivotResponse.getAggregations());
                                                           ObjectMapper mapperObj = new ObjectMapper();
                                                           String jsonString = mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                                                           JsonNode objectNode = mapperObj.readTree("{ \"data\":"+jsonString + "}");
                                                           PivotSNCRFlattener pivotSNCRFlattener = new PivotSNCRFlattener(proxy.getSqlBuilder());
                                                           List<Object> flatData= pivotSNCRFlattener.parseData(objectNode);
                                                           proxy.setData(flatData);
                                                           proxy.setStatusMessage("Data has been retrieved & has been flattened.");
                                                        logger.debug("Data from Aggregation converted into Flat Data " +flatData);
                                                   }else{
                                                     logger.debug("Data from Aggregation :" +sncrPivotResponse.getAggregations());
                                                       ObjectMapper mapperObj = new ObjectMapper();
                                                       String jsonString = mapperObj.writeValueAsString(sncrPivotResponse.getAggregations());
                                                       JsonNode objectNode = mapperObj.readTree("{ \"data\":"+jsonString + "}");
                                                       PivotSNCRFlattener pivotSNCRFlattener = new PivotSNCRFlattener(proxy.getSqlBuilder());
                                                       List<Map<String, Object>> flatData= pivotSNCRFlattener.parseDataMap(objectNode);
                                                       List<Object> data = new ArrayList<>();
                                                       List<Object> tabularData = StorageProxyUtils.getTabularFormat(flatData, StorageProxyUtils.COMMA); 
                                                       for (Object obj : tabularData){
                                                         data.add(obj);
                                                       }
                                                       proxy.setData(data);
                                                       proxy.setStatusMessage("Data has been retrieved & has been flattened.");
                                                     logger.debug("Data from Aggregation into JSON Format " +sncrPivotResponse.getAggregations()); 
                                                       }
                                                   }
                                                  else {
                                                    proxy.setStatusMessage("size cannot be greater or less than zero.");
                                                  }
                                                  }
                                                  else {
                                                    proxy.setStatusMessage("Please provide size with value 'size=0' & it shall not conti 'from' parameter in query.");
                                                    proxy.setPageSize(0);
                                                    proxy.setPageNum(0);
                                                  }
                                                } else {
                                                  proxy.setStatusMessage("To process the action type of sncrpivot, sqlBuilder is mandatory");
                                                }
                                               break;
                              case "search" : 
                                              Preconditions.checkArgument(proxy.getQuery()!=null, "Query cannot be null.");
                                              String query = proxy.getQuery();
                                              if(query.contains("size") && query.contains("from")){
                                                Pattern p = Pattern.compile(QUERY_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                                                Matcher m = p.matcher(query);
                                                if (m.find())
                                                {
                                                    String fromSize_1=m.group(1).trim();
                                                    String fromSize_1_Num=m.group(2);
                                                    String fromSize_2_Num=m.group(4);
                                                    if (fromSize_1.equals("size")){
                                                      proxy.setPageSize(Integer.parseInt(fromSize_1_Num));
                                                      proxy.setPageNum(Integer.parseInt(fromSize_2_Num)); 
                                                      }
                                                    else{
                                                      proxy.setPageSize(Integer.parseInt(fromSize_2_Num));
                                                      proxy.setPageNum(Integer.parseInt(fromSize_1_Num)); 
                                                    }
                                                } // parsing of size & from
                                              if(proxy.getPageSize()<= 50000){ 
                                              SearchESResponse<?> searchResponse =(SearchESResponse<?>) storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                                              long actualCount = searchResponse.getHits()!=null? searchResponse.getHits().getTotal() : 0;
                                              if (actualCount >0){
                                              proxy.setStatusMessage("Number of documents found for provided query :" + actualCount);
                                              List<Hit<?>> hits = searchResponse.getHits().getHits();
                                              List<Object> data = new ArrayList<>();
                                              if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                                 for (Hit<?> hit : hits){
                                                 data.add(hit.getSource());
                                                }
                                                proxy.setData(data);
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
                                              proxy.setData(data);
                                              }
                                              else {
                                                proxy.setStatusMessage("There are no documents available with the provided query.");
                                              }
                                              } // end of check for the size 50000
                                              else {
                                                proxy.setStatusMessage("The size cannot be greater than 50000");
                                              }
                                              }
                                              else{
                                                proxy.setStatusMessage("Please provide size or both parameter in query.");
                                                proxy.setPageSize(0);
                                                proxy.setPageNum(0);
                                              }
                                              break;
                              case "aggregate" :  
                                Preconditions.checkArgument(proxy.getQuery()!=null, "Query cannot be null.");
                                String aggregateQuery = proxy.getQuery();
                                setSize(0);
                                proxy.setPageSize(0);
                                proxy.setPageNum(0);
                                if(aggregateQuery.contains("size")){
                                  Pattern p = Pattern.compile(SIZE_REG_EX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
                                  Matcher m = p.matcher(aggregateQuery);
                                  if (m.find())
                                  {
                                      String fromSize_1=m.group(1).trim();
                                      String fromSize_1_Num=m.group(2);
                                      
                                      if (fromSize_1.equals("size")){
                                        setSize(Integer.parseInt(fromSize_1_Num));
                                        }
                                      else{
                                        setSize(0);
                                  } // parsing of size & from
                                  }
                                    SearchESResponse<?> sncrPivotResponse =(SearchESResponse<?>) storageConnectorService.searchDocuments(proxy.getQuery(), proxy);
                                    if (proxy.getResultFormat().value().equals(ResultFormat.JSON.value())){
                                        logger.debug("Data from Aggregation :" +sncrPivotResponse.getAggregations());
                                            if (sncrPivotResponse.getAggregations()!=null) {
                                            proxy.setAggregationData(sncrPivotResponse.getAggregations());}
                                            // This else block is for special case to handle if the same request expects both aggregate & search i.e. ANALYZE module
                                            else {
                                              List<Hit<?>> hits = sncrPivotResponse.getHits().getHits();
                                              List<Object> data = new ArrayList<>();
                                                 for (Hit<?> hit : hits){
                                                 data.add(hit.getSource());
                                                }
                                                proxy.setData(data);
                                            }
                                            }
                                    else {
                                      proxy.setStatusMessage("Aggregate action's does not support flattening of data yet.");
                                    }
                                 
                           } // end of aggregate size block 
                                else{
                                  proxy.setStatusMessage("Please provide size parameter in query.");
                                  proxy.setPageSize(0);
                                  proxy.setPageNum(0);
                           }      
                          break; 
                          } // end of action
                        } // // end of action operation else block
            } // end of action operation  if block
            else {
              proxy.setStatusMessage("This "+action+" is not yet supported by StorageType :" + storageType);
            }
              break;
              
          case "DL" :  proxy.setStatusMessage("Not supported. This feature is yet to be implemented");break;
              // Below are the steps for future implementation to support other storage implementation
              // a)
              // TODO: Storage Type : DL
              // TODO: Validate Spark Query (executing API & prepare the response with for validation using grammar file)
              //       (https://github.com/apache/spark/blob/master/sql/catalyst/src/main/antlr4/org/apache/spark/sql/catalyst/parser/SqlBase.g4)
              // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
              // TODO: Convert data either into tabular or JSON
          
          case  "RDMS": proxy.setStatusMessage("Not supported. This feature is yet to be implemented");break;
              // TODO: Storage Type : RDBMS (MYSQL/Oracle)
              // b)
              // TODO: Validate PL/SQL Query (executing API & prepare the response with for validation using grammar file)
              //       (https://github.com/antlr/grammars-v4/tree/master/mysql)
              //       (https://github.com/antlr/grammars-v4/tree/master/plsql)
              // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
              // TODO: Convert data either into tabular or JSON
          
          case  "METADATA":
                     String actionMetadata = proxy.getAction().value();
                     if (actionMetadata.equals(Action.CREATE.value()) || actionMetadata.equals(Action.READ.value()) || actionMetadata.equals(Action.UPDATE.value()) 
                         || actionMetadata.equals(Action.DELETE.value())){
                       switch (actionMetadata) {
                         case "create" : proxy= storageProxyMetaDataService.createEntryInMetaData(proxy); break;
                         case "update" : proxy= storageProxyMetaDataService.updateEntryFromMetaData(proxy);break;
                         case "delete" : proxy= storageProxyMetaDataService.deleteEntryFromMetaData(proxy);break;
                         case "read" :   proxy= storageProxyMetaDataService.readEntryFromMetaData(proxy);break;
                       }
                     }
                     else {
                       proxy.setStatusMessage("This "+actionMetadata+" is not yet supported by StorageType :" + storageType);
                     }
        } // end of switch statement
    response = StorageProxyUtils.prepareResponse(proxy, proxy!=null? proxy.getStatusMessage():"data is retrieved.");
    } // end of schema validation if block
    else {
      response = StorageProxyUtils.prepareResponse(proxy, "provided JSON input is not valid.");
    }
    return response;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
  
}
