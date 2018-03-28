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

public class WorkbenchJob implements Job<Integer> {

    private static final long serialVersionUID = 1L;
    private final NGContext ngctx;

    public WorkbenchJob(NGContext ngctx) {
        this.ngctx = ngctx;
    }

    @Override
    public Integer call(JobContext jobContext) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass().getName());
        log.debug("Start Workbench job");

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
