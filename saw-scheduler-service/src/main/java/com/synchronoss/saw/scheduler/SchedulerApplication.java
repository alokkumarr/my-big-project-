package com.synchronoss.saw.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SAW Scheduler Service main application
 *
 * Triggers execution of analyses that are set to run at certain
 * specific intervals.
 */
@SpringBootApplication
public class SchedulerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
