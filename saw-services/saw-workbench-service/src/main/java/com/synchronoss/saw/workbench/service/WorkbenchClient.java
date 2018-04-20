package com.synchronoss.saw.workbench.service;

import java.io.File;

import java.net.URI;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobHandle;
import com.cloudera.livy.LivyClient;
import com.cloudera.livy.LivyClientBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workbench client for submitting Livy jobs
 */
public class WorkbenchClient {
    private static final String WORKBENCH_JAR =
        "/opt/bda/saw-workbench-service/saw-workbench-spark.jar";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private LivyClient client;

    public WorkbenchClient(String livyUri) throws Exception {
        client = new LivyClientBuilder().setURI(new URI(livyUri)).build();
        log.debug("Uploading Workbench JAR");
        client.uploadJar(new File(WORKBENCH_JAR));
        log.debug("Uploaded Workbench JAR");
    }

    /**
     * Submits given Workbench job to Livy
     */
    public void submit(Job job) throws Exception {
        submit(job, null);
    }

    /**
     * Submits given Workbench job to Livy with given failure handler
     */
    public void submit(Job job, FailureHandler failure) throws Exception {
        log.info("Submitting job");
        JobHandle<Integer> jobHandle = client.submit(job);
        jobHandle.addListener(new WorkbenchJobListener(client, failure));
        log.info("Submitted job");
    }

    /**
     * Handles failure to execute Workbench job, for example marking
     * the related entity (dataset or preview) as failed
     */
    interface FailureHandler {
        void apply();
    }

    private class WorkbenchJobListener implements JobHandle.Listener<Integer> {
        private LivyClient client;
        private FailureHandler failure;

        WorkbenchJobListener(LivyClient client, FailureHandler failure) {
            this.client = client;
            this.failure = failure;
        }

        @Override
        public void onJobSucceeded(
            JobHandle<Integer> job, Integer result) {
            client.stop(true);
            log.info("Workbench job succeeded");
        }

        @Override
        public void onJobStarted(JobHandle<Integer> job) {
            log.info("Workbench job starting");
        }

        @Override
        public void onJobQueued(JobHandle<Integer> job) {
            log.info("Workbench job queued");
        }

        @Override
        public void onJobFailed(
            JobHandle<Integer> job, Throwable cause) {
            client.stop(true);
            log.error("Workbench job failed", cause);
            /* If failure handler was set, invoke it */
            if (failure != null) {
                failure.apply();
            }
        }

        @Override
        public void onJobCancelled(JobHandle<Integer> job) {
            client.stop(true);
            log.info("Workbench job cancelled");
            /* If failure handler was set, invoke it */
            if (failure != null) {
                failure.apply();
            }
        }
    }
}
