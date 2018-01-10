package com.synchronoss.saw.storage.proxy.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;

@Service
public class StorageProxyServiceImpl implements StorageProxyService {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyServiceImpl.class);
  
  @Value("${schema.file}")
  private String schemaFile;

  @Override
  public StorageProxyResponse execute(StorageProxyNode proxy) throws JsonProcessingException, IOException, ProcessingException {
    logger.debug("Executing data storage proxy for {}", proxy.getProxy().size()); 
    logger.trace("Validating Schema is started"); 
    Boolean validate = StorageProxyUtils.jsonSchemaValidate(proxy, schemaFile);
    logger.trace("Validating Schema is finished"); 
    StorageProxyResponse response =null;
    if (validate){
    
    // TODO: Storage Type : ES
    // TODO: Validate ES Query (executing API & prepare the response with for validation)
    // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular) 
    // TODO: Convert data either into tabular or JSON
    
    
      
    // Below are the steps for future implementation to support other storage implementation
    // a)
    // TODO: Storage Type : DL
    // TODO: Validate Spark Query (executing API & prepare the response with for validation using grammar file)
    //       (https://github.com/apache/spark/blob/master/sql/catalyst/src/main/antlr4/org/apache/spark/sql/catalyst/parser/SqlBase.g4)
    // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
    // TODO: Convert data either into tabular or JSON    
   
    
    
    // TODO: Storage Type : RDBMS (MYSQL/Oracle)
    // b)
    // TODO: Validate PL/SQL Query (executing API & prepare the response with for validation using grammar file)
    //       (https://github.com/antlr/grammars-v4/tree/master/mysql)
    //       (https://github.com/antlr/grammars-v4/tree/master/plsql)
    // TODO: Execute Query or perform create, delete, update operation & Prepare response based on the specification (either JSON or Tabular)  
    // TODO: Convert data either into tabular or JSON    
    
    response = StorageProxyUtils.prepareResponse(proxy.getProxy(), "data is retrieved");
    }
    else {
      response = StorageProxyUtils.prepareResponse(proxy.getProxy(), "provided JSON input is not valid.");
    }
    return response;
  }
  
}
