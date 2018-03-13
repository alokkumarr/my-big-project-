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
        log.debug("Uploading Workbench JAR");
        client.uploadJar(new File(WORKBENCH_JAR));
        log.info("Submitting Workbench job");
        JobHandle<Integer> job = client.submit(
            new WorkbenchJob(root, project, component, config));
        job.addListener(new JobHandle.Listener<Integer>() {
          @Override
          public void onJobSucceeded(JobHandle<Integer> job, Integer result) {
            client.stop(true);
            log.info("Workbench job successfully completed");
          }
          
          @Override
          public void onJobStarted(JobHandle<Integer> job) {
            log.info("Starting Workbench job");}
          
          @Override
          public void onJobQueued(JobHandle<Integer> job) {
            log.info("Workbench job queued");}
          
          @Override
          public void onJobFailed(JobHandle<Integer> job, Throwable cause) {
            client.stop(true);
          }
          
          @Override
          public void onJobCancelled(JobHandle<Integer> job) {
            client.stop(true);
          }
        });
        log.info("Submitted job");
    }
}
