package com.synchronoss.saw.workbench.service;

import sncr.xdf.component.Component;
import sncr.xdf.context.Context;
import sncr.xdf.parser.Parser;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class WorkbenchJob implements Job<Integer> {
    private static final long serialVersionUID = 1L;
    private final String root;
    private final String project;
    private final String component;
    private final String config;

    public WorkbenchJob(String root, String project, String component,
                        String config) {
        this.root = root;
        this.project = project;
        this.component = component;
        this.config = config;
    }

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        log.debug("Start Workbench job");
        String batch = "batch-" + Instant.now().toEpochMilli();
        Component xdfComponent;
        if (component.equals("parser")) {
            xdfComponent = new Parser() {
                @Override
                public void initSpark(Context ctx) {
                    try {
                        ctx.sparkSession = jobContext.sparkSession();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } else {
            throw new IllegalArgumentException(
                "Unknown component: " + component);
        }
        log.debug("Finished Workbench job");
        return Component.startComponent(
            xdfComponent, root, config, project, batch);
    }
}
