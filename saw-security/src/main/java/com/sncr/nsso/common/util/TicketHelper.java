/**
 * 
 */
package com.sncr.nsso.common.util;

import java.io.StringWriter;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sncr.nsso.app.repository.UserRepository;
import com.sncr.nsso.common.bean.Ticket;
import com.sncr.nsso.common.bean.User;

/**
 * @author vaibhav.kapoor
 * 
 */
public class TicketHelper {
	private static final Logger logger = LoggerFactory
			.getLogger(TicketHelper.class);
	
	public UserRepository userRepository;	

	public TicketHelper(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	/*	public Ticket_t getTicket(String ticketId) {
		Ticket_t ticket = null;
		String dumpPath = ConfigurationData.getInstance().getTicketDumpLocation();
		String fileName = dumpPath + File.separator + ticketId + ".xml";
		try {
			LogManager.log(LogManager.CAT_TICKET_UTIL, LogManager.LEVEL_INFO,
					"Fetching Details for ticket with id : "+ticketId,null,ticketId);
			JAXBContext context = JAXBContext.newInstance(Ticket_t.class);
			final File file = new File(fileName);
			Unmarshaller um = context.createUnmarshaller();
			ticket = (Ticket_t) um.unmarshal(file);
			LogManager.log(LogManager.CAT_TICKET_UTIL, LogManager.LEVEL_INFO,
					"Got Details : "+ticket.toString(), null,ticket.getTicketId());
		} catch (JAXBException e) {
			LogManager.log(LogManager.CAT_TICKET_UTIL, LogManager.LEVEL_ERROR,
					e.getMessage(),null,ticketId,e);
		}
		return ticket;
	}*/

	public void cleanseRepository(int preservePeriod) {

	}

	public void moveTicketToDumpLocation(String ticketId) {

	}

	public Ticket createTicket(User user, Boolean isReCreate) throws Exception{
		Ticket ticket = null;
		try		{
			if(!isReCreate){
				prepareTicketDetails(user);
			}
			// create ticket xml
			logger.debug(
					"Preparing ticket for user: "+user.getMasterLoginId(),user.getMasterLoginId(),null);
			ticket = prepareTicket(user);
			//inserting the ticket  detail into DB, commenting code to update xml in file path
			insertTicketDetails(ticket);
			logger.debug(
					"Saving ticket with id : "+ticket.getTicketId()+", is DONE",null,ticket.getTicketId());
			}
		catch(Exception e)		{
			logger.error(
					"Exception  occured saving ticket with id : "+user.getLoginId(),user.getMasterLoginId(),null);
			throw e;
		}
		return ticket;
	}

	private void prepareTicketDetails(User user) {
		userRepository.prepareTicketDetails(user);
	
}

	public String logout(String ticketId) {
		String newTicket = null;
		try {
			//logger.info("inactivating the ticket for ticket Id: " + ticketId);
			// update the ticket validity into DB
			userRepository.invalidateTicket(ticketId, "User Logged Out");
			logger.info("Successfully inactivated the Ticket ticket Id: " + ticketId, null, null);
			newTicket = "Successfully inactivated the ticket";
		} catch (Exception e) {
			logger.error("Exception occured while recreating the ticket: "+e.getMessage(), ticketId,  e);
		}
		return newTicket;
	}
	
	
	public Ticket reCreateTicket(String ticketId, Long validMins) {
		Ticket newTicket = null;
		try {
			Ticket oldTicket = userRepository.getTicketDetails(ticketId);
			logger.info("Recreating the ticket for ticket Id: " + ticketId, oldTicket.getMasterLoginId());
			// update the ticket validity into DB
			userRepository.invalidateTicket(ticketId, "Ticket Expired");
			logger.info("Got Details : " + ticketId, null, null);
			newTicket = prepareTicket(oldTicket, validMins);
			insertTicketDetails(newTicket);
			logger.info("Successfully recreated the Ticket for master Login Id: " + newTicket.getMasterLoginId(), newTicket.getTicketId());
		} catch (Exception e) {
			logger.error("Exception occured while recreating the ticket: "+e.getMessage(), ticketId,  e);
		}
		return newTicket;
	}

	public Ticket inactivateTicketChangePwd(String ticketId) {
		Ticket newTicket = null;
		try {
			Ticket oldTicket = userRepository.getTicketDetails(ticketId);
			logger.info("reCreateTicketChangePwd the ticket for ticket Id: " + ticketId, oldTicket.getMasterLoginId());
			// update the ticket validity into DB
			userRepository.invalidateTicket(ticketId, "Inactivated the ticket, inorder to change password");
		} catch (Exception e) {
			logger.error("Exception occured while recreating the ticket: "+e.getMessage(), ticketId,  e);
		}
		return newTicket;
	}
	
	
	public Ticket reCreateTicketChangePwd(String ticketId, Long validMins) {
		Ticket newTicket = null;
		try {
			Ticket oldTicket = userRepository.getTicketDetails(ticketId);
			logger.info("reCreateTicketChangePwd the ticket for ticket Id: " + ticketId, oldTicket.getMasterLoginId());
			// update the ticket validity into DB
			userRepository.invalidateTicket(ticketId, "Inactivated the ticket, inorder to change password");
			logger.info("Got Details : " + ticketId, null, null);
			newTicket = prepareTicket(oldTicket, validMins);
			insertTicketDetails(newTicket);
			logger.info("Successfully created the Ticket for master Login Id: " + newTicket.getMasterLoginId(), newTicket.getTicketId());
		} catch (Exception e) {
			logger.error("Exception occured while recreating the ticket: "+e.getMessage(), ticketId,  e);
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
				ticketResult =  writer.toString();
			} catch (JAXBException e) {
				logger.error( e.getMessage(), e);
			} 
			return ticketResult;
	}
	
	
	private Ticket prepareTicket(User user) {
		Random random = new Random();
		Ticket ticket = new Ticket();
		String ticketId = Thread.currentThread().getId() + "_"
				+ System.currentTimeMillis() + "_"
				+ random.nextInt(Integer.MAX_VALUE);
		ticket.setTicketId(ticketId);
		String winId = Thread.currentThread().getId() + "_"
				+ System.currentTimeMillis() + "_"
				+ random.nextInt(Integer.MAX_VALUE);
		ticket.setWindowId(winId);
		ticket.setMasterLoginId(user.getMasterLoginId());
		Long createdTime = System.currentTimeMillis();
		ticket.setCreatedTime(createdTime);
		// P2: take the validTill value from sso
		ticket.setValidUpto(createdTime + (user.getValidMins() * 60 * 1000));
		ticket.setValid(true);
		ticket.setValidityReason("User Authenticated Successfully");
		ticket.setProdCode(user.getTicketDetails().getLandingProd());
		ticket.setRoleType(user.getTicketDetails().getRoleType());
		ticket.setUserName(user.getTicketDetails().getUserName());
		ticket.setProducts(user.getTicketDetails().getProducts());
		ticket.setDataSecurityKey(user.getTicketDetails().getDataSKey());
		return ticket;
	}
	
	private Ticket prepareTicket(Ticket ticket, Long validMins) {
		Random random = new Random();
		String ticketId = Thread.currentThread().getId() + "_"
				+ System.currentTimeMillis() + "_"
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
	
	private void insertTicketDetails(Ticket ticket) throws Exception{
		try{
		userRepository.insertTicketDetails(ticket);
		}
		catch(Exception e){
			logger.error("Exception occured while creating ticket for user "+ticket.getMasterLoginId(),ticket.getMasterLoginId(),null, e);
			throw e;
		}
	}


}
