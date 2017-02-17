package com.razor.raw.re.listener;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.razor.raw.core.dao.repository.ReportExecutionLogRepository;
import com.razor.raw.core.req.ReportReq;
import com.razor.raw.re.properties.REProperties;
import com.razor.raw.re.report.process.ReportProcessor;


/**
 * 
 * @author surendra.rajaneni
 *
 */
//@EnableConfigurationProperties(REProperties.class)
@Component
public class ReportRequestListener implements MessageListener {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportRequestListener.class);
	
	@Autowired
	REProperties reProperties;
	@Autowired
	ReportProcessor reportProcessor;
	@Autowired
	ReportExecutionLogRepository reportExecutionLogRepository;

	private Session session = null;
		@Override
	public void onMessage(Message message) {
			logger.debug("User activity started here:" + this.getClass().getName()	+ " onMessage method");
		try {
			if (message != null) {
				Message receiveMessage = (Message) message;
				if (message instanceof ObjectMessage) {
					ObjectMessage oMesg = (ObjectMessage) receiveMessage;
					Serializable object = oMesg.getObject();
					if (object != null && object instanceof ReportReq) {
						ReportReq reportReq = (ReportReq) object;
						logger.debug("------------------------------>>>>>>>>>>>>>>>>>>>>>>>>>>"+reportReq.getReport().getReportId());
						reportExecutionLogRepository.insertReportExecutionLog(reportReq.getReport().getReportId(), "message Received with report description : "+reportReq.getName(), reportReq.getPublishedBy());
						reportProcessor.receiveMessage(reportReq);
					}
					else
					{
						reportExecutionLogRepository.insertReportExecutionLog(0, "unrecognaised message type "+receiveMessage.toString(), "");
						logger.debug("unrecognaised message type"+receiveMessage.toString());
					}
					
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName(), e);
		} finally {
			if (session != null) {
				try {
					message.clearBody();
					session.commit();
				} catch (JMSException e) {
					logger.error("Exception occured at " + this.getClass().getName(), e);
				} catch (Exception e) {
					logger.error("Exception occured at " + this.getClass().getName(), e);
				}
			}
		}
		logger.debug("User activity ended here:" + ReportRequestListener.class	+ " onMessage method");
	}

	public void receive() throws Exception {
		logger.debug("User activity started here:" + this.getClass().getName()	+ " receive method");

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(reProperties.getBrokerUrl());
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			Destination des = session.createQueue(reProperties.getQueue());
			MessageConsumer consumer = session.createConsumer(des);
			consumer.setMessageListener(this);
		} catch (Exception e) {
			logger.error("Exception occured at " + this.getClass().getName(), e);
		} finally {
			if (session != null) {
				try {
					session.commit();
				} catch (Exception e) {
					logger.error("Exception occured at " + this.getClass().getName(), e);
				}
			}
		}
		logger.debug(this.getClass().getName()+" receive method completed ");
	}
	
	public boolean isValid()
	{
		return (StringUtils.isEmpty(reProperties.getBrokerUrl()) || StringUtils
				.isEmpty(reProperties.getQueue())) ? false : true;
	}

	/**
	 * The entry point of the Razor Report Engine
	 * 
	 * @param arg
	 */
/*	public static void main(String arg[]) {
		
		try {
			new ReportRequestListener().receive();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(	"Report Engine started is shutting down");
	}*/
}
