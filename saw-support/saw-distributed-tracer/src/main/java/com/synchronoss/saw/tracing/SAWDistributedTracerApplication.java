package com.synchronoss.saw.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import zipkin.server.EnableZipkinServer;

@EnableDiscoveryClient
@EnableZipkinServer
@SpringBootApplication

public class SAWDistributedTracerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SAWDistributedTracerApplication.class, args);
	}
}
