package com.synchronoss.saw.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * SAW Scheduler Service main application
 *
 * Triggers execution of analyses that are set to run at certain
 * specific intervals.
 */
@SpringBootApplication
public class SchedulerApplication implements CommandLineRunner {
    private static final String ANALYSIS_URL =
        /* Workaround: Assume same host and fixed port until discovery
         * service in use */
        "http://localhost:9200/analysis";

    private final Logger log = LoggerFactory.getLogger(
        SchedulerApplication.class.getName());

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting");
        /* Validate argument list */
        if (args.length != 1) {
            throw new ExitException(
                "Unexpected number of arguments: expected 1, got "
                + args.length, 1);
        }
        String type = args[0];
        log.info("Type: {}", type);
        String executionId = getCurrentExecutionId(type);
        log.info("Execution ID: {}", executionId);
        /* Get the list of analyses that have a schedule for the given
         * execution type, for example hourly or daily, from the
         * Analysis Service */
        RestTemplate restTemplate = new RestTemplate();
        String url = ANALYSIS_URL + "/scheduled/" + type;
        String[] analysisIds = restTemplate.getForObject(
            url, String.class).split(" ");
        try (SchedulerStore store = new SchedulerStore()) {
            log.info("Processing analyses");
            for (String analysisId : analysisIds) {
                processAnalysis(store, executionId, analysisId);
            }
        }
        log.info("Finished");
    }

    private void processAnalysis(
        SchedulerStore store, String executionId, String analysisId) {
        log.info("Process analysis: {}", analysisId);
        /* Get the last execution ID from the persistent store */
        String lastExecutionId = store.getLastExecutionId(analysisId);
        boolean execute = false;
        log.info("Last execution ID: {}",
                 lastExecutionId == null ? "not found" : lastExecutionId);
        if (executionId == null) {
            /* If analysis has never been executed before, execute it
             * for the first time now */
            execute = true;
            log.info("Execute: {}, because never executed before", execute);
        } else {
            /* If analysis has previously been executed, execute it
             * only if the current invocation is for a new period.
             * This mechanism of comparing execution IDs ensures that
             * an analysis is executed only once within each
             * period.  */
            execute = (!executionId.equals(lastExecutionId));
            log.info("Execute: {}, by comparing execution IDs", execute);
        }
        if (execute) {
            /* Post execute request to Analysis Service */
            String url = ANALYSIS_URL + "/" + analysisId + "/executions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                org.springframework.http.MediaType.APPLICATION_JSON);
            String body = "{type: \"scheduled\"}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(url, entity, String.class);
            /* Store the last execution ID to prevent from executing a
             * further times during the same period */
            log.info("Set last execution ID: {}", executionId);
            store.setLastExecutionId(analysisId, executionId);
        }
    }

    /**
     * Calculate an execution ID that identifies the current period
     * which can be used to ensure that an analysis is only executed
     * once within the period
     */
    private String getCurrentExecutionId(String type) {
        String pattern = null;
        if (type.equals("daily")) {
            pattern = "yyyyMMdd";
        } else if (type.equals("hourly")) {
            pattern = "yyyyMMddHH";
        } else {
            throw new IllegalArgumentException(
                "Unknown execution type: " + type);
        }
        ZonedDateTime date = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return String.format(
            "%s-%s", type.toUpperCase(), date.format(formatter));
    }
}
