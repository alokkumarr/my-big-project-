package com.synchronoss.saw.observe.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.observe.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure;

@Service
class ObserveServiceImpl implements ObserveService{
  
  private static final Logger logger = LoggerFactory.getLogger(ObserveServiceImpl.class);

  @Value("${metastore.base}")
  private String basePath;

  
  @Override
  public ObserveResponse addDashboard(Observe node) throws JSONValidationSAWException, CreateEntitySAWException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    //metaDataStoreStructure.set
    return null;
  }

  @Override
  public ObserveResponse getDashboardbyCriteria(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObserveResponse listOfDashboardByCriteria(int size)
      throws JSONValidationSAWException, ReadEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObserveResponse updateDashboard(Observe node) throws JSONValidationSAWException, UpdateEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObserveResponse deleteDashboard(Observe node) throws JSONValidationSAWException, DeleteEntitySAWException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ObserveResponse generateId() throws JSONValidationSAWException {

    String id = UUID.randomUUID().toString() + delimiter + PortalDataSet + delimiter
        + System.currentTimeMillis();
    ObserveResponse response = new ObserveResponse();
    response.setId(id);
    return response;
  }

  

}
