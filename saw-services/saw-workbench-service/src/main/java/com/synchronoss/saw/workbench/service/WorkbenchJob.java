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
    private final String conf;

    public WorkbenchJob(String root, String project, String conf) {
        this.root = root;
        this.project = project;
        this.conf = conf;
    }

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        log.debug("Start Workbench job");
        String batch = "batch-" + Instant.now().toEpochMilli();
        Parser parser = new Parser() {
                @Override
                public void initSpark(Context ctx) {
                    try {
                        ctx.sparkSession = jobContext.sparkSession();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        log.debug("Finished Workbench job");
        return Component.startComponent(parser, root, conf, project, batch);
    }
}
