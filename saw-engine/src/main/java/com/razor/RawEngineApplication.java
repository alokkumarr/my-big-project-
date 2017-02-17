package com.razor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.razor.raw.re.listener.ReportRequestListener;
import com.razor.raw.re.properties.REProperties;


@SpringBootApplication
@SpringApplicationConfiguration(classes = {REProperties.class})
public class RawEngineApplication {
	private static final Logger logger = LoggerFactory
			.getLogger(RawEngineApplication.class);
    public static void main(String[] args) {
    	logger.debug("started ...............");
    	 ConfigurableApplicationContext context =  SpringApplication.run(RawEngineApplication.class, args);
    	ReportRequestListener reportRequestListener =   context.getBean(ReportRequestListener.class);
    	try {
    		if(reportRequestListener.isValid())
    			reportRequestListener.receive();
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println(reportRequestListener);
        System.out.println("started");
        
        logger.debug("ended ...............");
    }

}
