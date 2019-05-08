package com.synchronoss.saw.scheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import static junit.framework.TestCase.fail;


@SpringBootTest
public class QuartzSchedulerApplicationTests {

	@Test
	public void contextLoads() {
	}

    private Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(QuartzSchedulerApplicationTests.class);

    @Before
    public void setUp() throws SchedulerException {

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");
        scheduler = sf.getScheduler();
        logger.info("------- Initialization Complete -----------");
        logger.info("------- Starting Scheduler ----------------");
        // start the schedule
        scheduler.start();
    }

    @Test
    public void checkScheduler() throws SchedulerException {
        if (!scheduler.isStarted()) {
            fail("scheduler is failed to start");
        }
    }

    @After
    public void tearDown() throws SchedulerException {
        logger.info("------- Shutting down Scheduler ----------------");
        scheduler.shutdown();

    }

}
