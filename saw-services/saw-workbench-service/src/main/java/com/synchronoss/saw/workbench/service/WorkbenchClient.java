package com.synchronoss.saw.workbench.service;

import com.cloudera.livy.JobHandle;
import com.cloudera.livy.LivyClient;
import com.cloudera.livy.LivyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;

public class WorkbenchClient {
    private static final String WORKBENCH_JAR =
        "/opt/bda/saw-workbench-service/saw-workbench-spark.jar";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    public void submit(String livyUri, String root, String project,
                       String component, String config) throws Exception {
        LivyClient client = new LivyClientBuilder()
            .setURI(new URI(livyUri))
            .build();
        try {
            log.debug("Uploading Workbench JAR");
            client.uploadJar(new File(WORKBENCH_JAR));
            log.info("Submitting Workbench job");
            JobHandle<Integer> job = client.submit(
                new WorkbenchJob(root, project, component, config));
            log.info("Submitted job");
            Integer result = job.get();
            log.info("Job result: {}", result);
        } finally {
            client.stop(true);
        }
    }
}
