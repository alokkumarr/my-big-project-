package com.synchronoss.saw.observe.service;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.exceptions.SipDeleteEntityException;
import com.synchronoss.saw.exceptions.SipJsonValidationException;
import com.synchronoss.saw.exceptions.SipReadEntityException;
import com.synchronoss.saw.exceptions.SipUpdateEntityException;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveResponse;

import java.util.List;

public interface ObserveService {

  String delimiter = "::";
  String Response = "Response : {}";
  String PortalDataSet = "PortalDataSet";
  String SUCCESS = "Entity has been retrieved successfully";

  ObserveResponse addDashboard(Observe node)
      throws SipJsonValidationException, SipCreateEntityException;

  ObserveResponse getDashboardbyCriteria(Observe node)
      throws SipJsonValidationException, SipReadEntityException;

  ObserveResponse updateDashboard(Observe node)
      throws SipJsonValidationException, SipUpdateEntityException;

  ObserveResponse deleteDashboard(Observe node)
      throws SipJsonValidationException, SipDeleteEntityException;

  String generateId() throws SipJsonValidationException;

  ObserveResponse getDashboardbyCategoryId(Observe node)
      throws SipJsonValidationException, SipReadEntityException;

  boolean haveValidAnalysis(List<Object> observeTiles, Ticket ticket);
}
