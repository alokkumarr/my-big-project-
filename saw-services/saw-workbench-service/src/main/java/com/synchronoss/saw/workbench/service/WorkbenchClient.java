package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import com.cloudera.livy.JobHandle;
import com.cloudera.livy.LivyClient;
import com.cloudera.livy.LivyClientBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sncr.bda.conf.ComponentConfiguration;

import sncr.xdf.context.ComponentServices;
import sncr.xdf.ngcomponent.NGContextServices;
import sncr.xdf.parser.AsynchNGParser;
import sncr.xdf.sql.ng.AsynchNGSQLComponent;
import sncr.xdf.transformer.ng.AsynchNGTransformerComponent;

public class WorkbenchClient {
    private static final String WORKBENCH_JAR =
        "/opt/bda/saw-workbench-service/saw-workbench-spark.jar";
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private NGContextServices ngCtxSvc;

    public WorkbenchClient(String componentName,
                           String root,
                           String project,
                           String config) throws Exception {
        String batch = "batch-" + Instant.now().toEpochMilli();
        ComponentConfiguration cfg = null;

        switch (componentName) {
        case "parser":
            ComponentServices[] pcs =
            {
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = AsynchNGParser.analyzeAndValidate(config);
            ngCtxSvc = new NGContextServices(
                pcs, root, cfg, project, componentName, batch);
            break;
        case "sql":
            ComponentServices[] scs =
            {
                ComponentServices.InputDSMetadata,
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = AsynchNGSQLComponent.analyzeAndValidate(config);
            ngCtxSvc = new NGContextServices(
                scs, root, cfg, project, componentName, batch);
            break;
        case "transformer":
            ComponentServices[] tcs =
            {
                ComponentServices.InputDSMetadata,
                ComponentServices.OutputDSMetadata,
                ComponentServices.Project,
                ComponentServices.TransformationMetadata,
                ComponentServices.Spark
            };
            cfg = AsynchNGTransformerComponent.analyzeAndValidate(config);
            ngCtxSvc = new NGContextServices(
                tcs, root, cfg, project, componentName, batch);
            break;
        default:
            throw new Exception("Unsupported component: " + componentName);
        }
        ngCtxSvc.initContext();
        ngCtxSvc.registerOutputDataSet();
    }

    public List<String> getDataSetIDs() {
        return ngCtxSvc.getNgctx().registeredOutputDSIds;
    }


    public void submit(String livyUri)
        throws Exception {
        LivyClient client = new LivyClientBuilder().setURI(
            new URI(livyUri)).build();
        log.debug("Uploading Workbench JAR");
        client.uploadJar(new File(WORKBENCH_JAR));
        log.info("Submitting Workbench job");
        JobHandle<Integer> job = client.submit(
            new WorkbenchJob(ngCtxSvc.getNgctx()));
        job.addListener(new JobHandle.Listener<Integer>() {

            @Override
            public void onJobSucceeded(
                JobHandle<Integer> job, Integer result) {
                client.stop(true);
                log.info("Workbench job successfully completed");
            }

            @Override
            public void onJobStarted(JobHandle<Integer> job) {
                log.info("Starting Workbench job");
            }

            @Override
            public void onJobQueued(JobHandle<Integer> job) {
                log.info("Workbench job queued");
            }

            @Override
            public void onJobFailed(
                JobHandle<Integer> job, Throwable cause) {
                client.stop(true);
                log.info("Workbench job Failed.");
            }

            @Override
            public void onJobCancelled(JobHandle<Integer> job) {
                client.stop(true);
                log.info("Workbench job Cancelled.");
            }
        });
        log.info("Submitted job");
    }
}
