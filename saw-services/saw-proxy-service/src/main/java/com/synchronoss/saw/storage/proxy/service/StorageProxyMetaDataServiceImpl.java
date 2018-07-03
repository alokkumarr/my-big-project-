package com.synchronoss.saw.storage.proxy.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.storage.proxy.StorageProxyUtils;
import com.synchronoss.saw.storage.proxy.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;


public class StorageProxyMetaDataServiceImpl implements StorageProxyMetaDataService {

  private static final Logger logger =
      LoggerFactory.getLogger(StorageProxyMetaDataServiceImpl.class);

  @Value("${metastore.base}")
  @NotNull
  private String basePath;
  
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";

  @Override
   public StorageProxy createEntryInMetaData(StorageProxy storageProxy) throws CreateEntitySAWException {
     logger.trace("createEntryInMetaData starts here:{}", storageProxy);
     String id = generateId();
     StorageProxy responseNode = new StorageProxy();
     storageProxy.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
     ObjectMapper mapper = new ObjectMapper();
     try {
       List<MetaDataStoreStructure> structure = StorageProxyUtils.node2JSONObject(storageProxy, basePath,
           id, Action.create, Category.StorageProxy);
       logger.trace("Before invoking request to MaprDB JSON store :{}",
           mapper.writeValueAsString(structure));
       MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
       requestMetaDataStore.process();
       BeanUtils.copyProperties(responseNode, storageProxy);
       responseNode.setStatusMessage("Entity " + id + " has been created successfully.");
       responseNode.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
     } catch (Exception ex) {
       logger.error("Problem on the storage while creating an entity", ex);
       throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
     }
      logger.trace("createEntryInMetaData ends here:{}", storageProxy);
      return responseNode;
    }

  @Override
  public StorageProxy readEntryFromMetaData(StorageProxy storageProxy) throws ReadEntitySAWException {
    return null;
  }

  @Override
  public StorageProxy updateEntryFromMetaData(StorageProxy storageProxy) throws UpdateEntitySAWException {
    return null;
  }


}
