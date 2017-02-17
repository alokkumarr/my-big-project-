package com.razor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.razor.distribution.email.MailSender;

@SpringBootApplication
public class RawDistributionApplication {

    public static void main(String[] args) {
    	ConfigurableApplicationContext context = SpringApplication.run(RawDistributionApplication.class, args);
    	System.setProperty("java.net.preferIPv4Stack" , "true");
    	
    	 MailSender mailSender =   context.getBean(MailSender.class);
    	 System.out.println(mailSender.isValid());
    	 List<String> list = new ArrayList<String>();
    	 list.add("D:\\rrm\\report.properties");
    	mailSender.sendMail("surendra.rajaneni@razorsight.com", "hai", "message", list);
    	
    }
}
