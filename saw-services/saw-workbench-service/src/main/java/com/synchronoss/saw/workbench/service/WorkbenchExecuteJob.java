package com.synchronoss.saw.workbench.service;

import java.time.Instant;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sncr.xdf.component.Component;
import sncr.xdf.context.Context;
import sncr.xdf.parser.Parser;
import sncr.xdf.sql.TempSQLComponent;

public class WorkbenchExecuteJob implements Job<Integer> {
    private static final long serialVersionUID = 1L;
    private final String root;
    private final String project;
    private final String component;
    private final String config;

    public WorkbenchExecuteJob(
        String root, String project, String component, String config) {
        this.root = root;
        this.project = project;
        this.component = component;
        this.config = config;
    }

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());

        log.info("Start execute job");
        AsynchAbstractComponent aac = null;
        switch (ngctx.componentName) {
        case "sql":
            aac = new AsynchNGSQLComponent(ngctx);
            break;
        case "parser":
            aac = new AsynchNGParser(ngctx);
            break;
        case "transformer":
            aac = new AsynchNGTransformerComponent(ngctx);
            break;
        default:
            throw new IllegalArgumentException("Unknown component: "
                + ngctx.componentName);
        }
        if (!aac.initComponent(jobContext.sc())) {
            log.error("Could not initialize component");
            return -1;
        }
        log.info("Starting Workbench job");
        int rc = aac.run();
        log.info("Workbench job completed, result: " + rc + " error: "
            + aac.getError());
        int status = Component.startComponent(
            xdfComponent, root, config, project, batch);
        if (status != 0) {
            throw new RuntimeException(
                "XDF returned non-zero status: " + status);
        }
        log.info("Finished execute job");
        return null;
    }
}
