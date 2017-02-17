package com.razor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RawConfigurationApplication {
	private static final Logger logger = LoggerFactory
			.getLogger(RawConfigurationApplication.class);
    public static void main(String[] args) {
    	//LogManager.log(LogManager.CATEGORY_DEFAULT, LogManager.LEVEL_DEBUG, "sakhdflkjashdkljhaskldjhaskljdh");
    	logger.debug("my first...............");
        SpringApplication.run(RawConfigurationApplication.class, args);
    }
}
