package com.synchronoss.saw.registry;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SAWDiscoveryServer 
{
    public static void main( String[] args )
    {
    	new SpringApplicationBuilder(SAWDiscoveryServer.class)
    	.web(true)
    	.run(args);
    }
}
