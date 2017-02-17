/**
 * 
 */
package com.razor.raw.generation.rest.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.razor.raw.core.jms.IQueueSender;
import com.razor.raw.core.req.ReportReq;
import com.razor.raw.generation.rest.properties.RGProperties;

/**
 * 
 * @author surendra.rajaneni
 *
 */

//@EnableConfigurationProperties(RGProperties.class)
@Component(value="reportRequestSender")
public class ReportRequestSender implements IQueueSender {
	private static final Logger logger = LoggerFactory
			.getLogger(ReportRequestSender.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 7798906447063709714L;
	@Autowired
	RGProperties rgProperties;
	@Override
	public void sendMessage(ReportReq reportReq) {
		logger.debug("User activity started here: "+this.getClass().getName() + " - sendMessage - START");
		
		Connection queueConnection = null;
		Session queueSession = null;
		MessageProducer producer = null;
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					rgProperties.getBrokerUrl());
			queueConnection = connectionFactory.createConnection();
			queueConnection.start();
			queueSession = queueConnection.createSession(true,
					Session.AUTO_ACKNOWLEDGE);
			Destination des = queueSession
					.createQueue(rgProperties.getQueue());
			producer = queueSession.createProducer(des);
			// Message is persistent if broker crashes and it will wait for two hrs
			// till then the message will not be lost
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.setTimeToLive(7200000);
			
			ObjectMessage objMesg = queueSession.createObjectMessage();
			objMesg.setObject(reportReq);
			producer.send(objMesg);
		} catch (Exception e) {
			logger.error("Exception occured at "+ this.getClass().getName() + "in sendMessage - ", e);
			e.printStackTrace();
		} finally {
			try {
				if(queueSession != null)
				queueSession.commit();
				if(producer != null)
				producer.close();
				if(queueSession != null)
				queueSession.close();
				if(queueConnection != null)
				queueConnection.close();

			} catch (JMSException e) {
				logger.error("Exception occured at "+ this.getClass().getName() + "in sendMessage - ", e);
				e.printStackTrace();
			}
		}
		logger.error("User activity ends here: "+ this.getClass().getName() + "in sendMessage - ");
	}
}
