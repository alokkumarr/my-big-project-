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
        log.debug("Start execute job");
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
        log.debug("Starting Workbench job");
        return aac.run();
    }
}
