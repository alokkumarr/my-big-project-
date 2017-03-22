package com.synchronoss.saw.monitor.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
//@EnableTurbineStream
@EnableHystrixDashboard
@EnableDiscoveryClient
public class SAWMonitoringDashboard 
{
    public static void main( String[] args )
    {
        SpringApplication.run(SAWMonitoringDashboard.class, args);
    }
}
