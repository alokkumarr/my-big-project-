package com.synchronoss.saw.observe.service;

import com.synchronoss.saw.observe.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.observe.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.observe.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.observe.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.observe.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;

public interface ObserveService {

  String delimiter = "::";
  String PortalDataSet = "PortalDataSet";
  public ObserveResponse addDashboard(Observe node) throws JSONValidationSAWException, CreateEntitySAWException;
  public ObserveResponse getDashboardbyCriteria(Observe node) throws JSONValidationSAWException, ReadEntitySAWException;
  public ObserveResponse listOfDashboardByCriteria(int size) throws JSONValidationSAWException, ReadEntitySAWException;
  public ObserveResponse updateDashboard(Observe node) throws JSONValidationSAWException, UpdateEntitySAWException;
  public ObserveResponse deleteDashboard(Observe node) throws JSONValidationSAWException, DeleteEntitySAWException;
  public ObserveResponse generateId() throws JSONValidationSAWException;
}

