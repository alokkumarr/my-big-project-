package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.User;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import java.io.StringWriter;
import java.security.SecureRandom;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class TicketHelperImpl implements TicketHelper {

  private static final Logger logger = LoggerFactory.getLogger(TicketHelper.class);
  @Autowired public UserRepository userRepository;

  public TicketHelperImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void cleanseRepository(int preservePeriod) {}

  public void moveTicketToDumpLocation(String ticketId) {}

  @Override
  public Ticket createTicket(User user, Boolean isReCreate) throws Exception {
    Ticket ticket = null;
    try {
      if (!isReCreate) {
        prepareTicketDetails(user, false);
      }
      // create ticket xml
      ticket = prepareTicket(user);
      // inserting the ticket detail into DB, commenting code to update
      // xml in file path
      insertTicketDetails(ticket);
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception  occured saving ticket with id : " + e.getMessage(), null, e);
      throw e;
    }
    return ticket;
  }

  @Override
  public Ticket createDefaultTicket(User user, Boolean onlyDef) throws Exception {
    Ticket ticket = null;
    try {
      prepareTicketDetails(user, onlyDef);
      // create ticket xml
      ticket = prepareTicket(user);
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception  occured saving ticket with id : " + e.getMessage(), null, e);
      throw e;
    }
    return ticket;
  }

  @Override
  public void prepareTicketDetails(User user, Boolean onlyDef) {
    userRepository.prepareTicketDetails(user, onlyDef);
  }

  @Override
  public String logout(String ticketId) {
    String newTicket = null;
    try {
      // logger.info("inactivating the ticket for ticket Id: " + ticketId);
      // update the ticket validity into DB
      userRepository.invalidateTicket(ticketId, "User Logged Out");
      newTicket = "Successfully inactivated the ticket";
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception occured while recreating the ticket: " + e.getMessage(), null, e);
    }
    return newTicket;
  }

  @Override
  public Ticket reCreateTicket(String ticketId, Long validMins) {
    Ticket newTicket = null;
    try {
      Ticket oldTicket = userRepository.getTicketDetails(ticketId);
      // update the ticket validity into DB
      userRepository.invalidateTicket(ticketId, "Ticket Expired");
      newTicket = prepareTicket(oldTicket, validMins);
      insertTicketDetails(newTicket);
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception occured while recreating the ticket: " + e.getMessage(), null, e);
    }
    return newTicket;
  }

  @Override
  public Ticket inactivateTicketChangePwd(String ticketId) {
    Ticket newTicket = null;
    try {
      @SuppressWarnings("unused")
      Ticket oldTicket = userRepository.getTicketDetails(ticketId);
      // update the ticket validity into DB
      userRepository.invalidateTicket(
          ticketId, "Inactivated the ticket, inorder to change password");
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception occured while recreating the ticket: " + e.getMessage(), null, e);
    }
    return newTicket;
  }

  @Override
  public Ticket reCreateTicketChangePwd(String ticketId, Long validMins) {
    Ticket newTicket = null;
    try {
      Ticket oldTicket = userRepository.getTicketDetails(ticketId);
      // update the ticket validity into DB
      userRepository.invalidateTicket(
          ticketId, "Inactivated the ticket, inorder to change password");
      newTicket = prepareTicket(oldTicket, validMins);
      insertTicketDetails(newTicket);
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception occured while recreating the ticket: " + e.getMessage(), null, e);
    }
    return newTicket;
  }

  public String createUpdateXML(Ticket ticket) {
    String ticketResult = null;
    try {
      JAXBContext context = JAXBContext.newInstance(Ticket.class);
      StringWriter writer = new StringWriter();
      Marshaller m = context.createMarshaller();
      m.marshal(ticket, writer);
      ticketResult = writer.toString();
    } catch (JAXBException e) {
      logger.error(e.getMessage(), e);
    }
    return ticketResult;
  }

  @Override
  public Ticket prepareTicket(User user) {
    SecureRandom random = new SecureRandom();
    Ticket ticket = new Ticket();
    String ticketId =
        Thread.currentThread().getId()
            + "_"
            + System.currentTimeMillis()
            + "_"
            + random.nextInt(Integer.MAX_VALUE);
    ticket.setTicketId(ticketId);
    String winId =
        Thread.currentThread().getId()
            + "_"
            + System.currentTimeMillis()
            + "_"
            + random.nextInt(Integer.MAX_VALUE);
    ticket.setWindowId(winId);
    ticket.setMasterLoginId(user.getMasterLoginId());
    Long createdTime = System.currentTimeMillis();
    ticket.setCreatedTime(createdTime);
    // P2: take the validTill value from sso
    ticket.setValidUpto(createdTime + (user.getValidMins() * 60 * 1000));
    ticket.setValid(true);
    ticket.setValidityReason("User Authenticated Successfully");
    if (user.getTicketDetails() != null) {
      ticket.setDefaultProdID(user.getTicketDetails().getLandingProd());
      ticket.setRoleCode(user.getTicketDetails().getRoleCode());
      ticket.setRoleType(user.getTicketDetails().getRoleType());
      ticket.setUserFullName(user.getTicketDetails().getUserFullName());
      ticket.setProducts(user.getTicketDetails().getProducts());
      ticket.setSipDskAttribute(user.getTicketDetails().getDskAttribute());
      ticket.setCustID(user.getTicketDetails().getCustID());
      ticket.setCustCode(user.getTicketDetails().getCustCode());
      ticket.setUserId(user.getTicketDetails().getUserId());
      ticket.setCustomConfig(user.getTicketDetails().getCustomConfig());
      ticket.setIsJvCustomer(user.getTicketDetails().getIsJvCustomer());
      ticket.setFilterByCustomerCode(user.getTicketDetails().getFilterByCustomerCode());
    }
    return ticket;
  }

  @Override
  public Ticket prepareTicket(Ticket ticket, Long validMins) {
    SecureRandom random = new SecureRandom();
    String ticketId =
        Thread.currentThread().getId()
            + "_"
            + System.currentTimeMillis()
            + "_"
            + random.nextInt(Integer.MAX_VALUE);
    ticket.setTicketId(ticketId);

    Long createdTime = System.currentTimeMillis();
    ticket.setCreatedTime(createdTime);
    // P2: take the validTill value from sso
    ticket.setValidUpto(createdTime + (validMins * 60 * 1000));
    ticket.setValid(true);
    ticket.setValidityReason("Ticket re-Created Successfully");
    return ticket;
  }

  @Override
  public void insertTicketDetails(Ticket ticket) throws Exception {
    try {
      userRepository.insertTicketDetails(ticket);
    } catch (DataAccessException de) {
      logger.error("Exception encountered while accessing DB : " + de.getMessage(), null, de);
      throw de;
    } catch (Exception e) {
      logger.error("Exception occured while creating ticket for user " + e.getMessage(), null, e);
      throw e;
    }
  }

  @Override
  public boolean checkTicketValid(String ticketID, String masterLoginId) {
    return userRepository.IsTicketValid(ticketID, masterLoginId);
  }
}
