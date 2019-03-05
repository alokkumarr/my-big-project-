package com.synchronoss.saw.observe.service;

import com.synchronoss.saw.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.exceptions.UpdateEntitySAWException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;

public interface ObserveService {

  String delimiter = "::";
  String PortalDataSet = "PortalDataSet";

  ObserveResponse addDashboard(Observe node)
      throws JSONValidationSAWException, CreateEntitySAWException;

  ObserveResponse getDashboardbyCriteria(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException;

  ObserveResponse updateDashboard(Observe node)
      throws JSONValidationSAWException, UpdateEntitySAWException;

  ObserveResponse deleteDashboard(Observe node)
      throws JSONValidationSAWException, DeleteEntitySAWException;

  String generateId() throws JSONValidationSAWException;

  ObserveResponse getDashboardbyCategoryId(Observe node)
      throws JSONValidationSAWException, ReadEntitySAWException;
}
