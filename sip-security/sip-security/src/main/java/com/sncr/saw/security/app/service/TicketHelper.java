/** */
package com.sncr.saw.security.app.service;

import com.sncr.saw.security.common.bean.User;
import com.synchronoss.bda.sip.jwt.token.Ticket;

public interface TicketHelper {

  Ticket createTicket(User user, Boolean isReCreate, String ticketId) throws Exception;

  Ticket createDefaultTicket(User user, Boolean onlyDef) throws Exception;

  void prepareTicketDetails(User user, Boolean onlyDef);

  String logout(String ticketId);

  Ticket reCreateTicket(String ticketId, Long validMins);

  Ticket inactivateTicketChangePwd(String ticketId);

  Ticket reCreateTicketChangePwd(String ticketId, Long validMins);

  Ticket prepareTicket(User user, String ticketId);

  Ticket prepareTicket(Ticket ticket, Long validMins);

  void insertTicketDetails(Ticket ticket) throws Exception;

  boolean checkTicketValid(String ticketID, String masterLoginId);
}
