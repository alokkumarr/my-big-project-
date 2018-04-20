package com.synchronoss.saw.workbench.service;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sncr.xdf.context.NGContext;

import sncr.xdf.ngcomponent.AsynchAbstractComponent;
import sncr.xdf.parser.AsynchNGParser;
import sncr.xdf.sql.ng.AsynchNGSQLComponent;
import sncr.xdf.transformer.ng.AsynchNGTransformerComponent;


public class WorkbenchExecuteJob implements Job<Integer> {
    private static final long serialVersionUID = 1L;
    private final String root;
    private final String project;
    private final String component;
    private final String config;
    private final NGContext ngctx;

    public WorkbenchExecuteJob(
        String root, String project, String component, String config) {
        this.root = root;
        this.project = project;
        this.component = component;
        this.config = config;
        ngctx = null;
    }

    public WorkbenchExecuteJob(NGContext ngctx) {
        this.root      = null;
        this.project   = null;
        this.component = null;
        this.config    = null;
        this.ngctx     = ngctx;
    }

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        log.info("Starting execute job");
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
        } else if (component.equals("sql")) {
            xdfComponent = new TempSQLComponent() {
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
