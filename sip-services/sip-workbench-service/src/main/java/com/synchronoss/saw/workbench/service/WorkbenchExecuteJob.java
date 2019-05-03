package com.synchronoss.saw.workbench.service;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
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

  /**
    * This is parameterized constructor.
   */
  public WorkbenchExecuteJob(String root, String project, String component, String config) {
    this.root = root;
    this.project = project;
    this.component = component;
    this.config = config;
    ngctx = null;
  }

  /**
   * This is parameterized.
   */
  public WorkbenchExecuteJob(NGContext ngctx) {
    this.root = null;
    this.project = null;
    this.component = null;
    this.config = null;
    this.ngctx = ngctx;
  }

  @Override
  public Integer call(JobContext jobContext) throws Exception {
    /* Workaround: If executed through Apache Livy the logging
     * level will be WARN by default and at the moment no way to
     * change that level through configuration files has been
     * found, so set it programmatically to at least INFO to help
     * troubleshooting */
    if (!LogManager.getRootLogger().isInfoEnabled()) {
        LogManager.getRootLogger().setLevel(Level.INFO);
    }
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
        throw new IllegalArgumentException("Unknown component: " + ngctx.componentName);
    }
    if (!aac.initComponent(jobContext.sc())) {
      log.error("Could not initialize component");
      throw new RuntimeException("Could not initialize component:");
    }
    log.info("Starting Workbench job");
    int rc = aac.run();
    log.info("Workbench job completed, result: " + rc + " error: " + aac.getError());

    if (rc != 0) {
      throw new RuntimeException("XDF returned non-zero status: " + rc);
    }
    return rc;
  }
}
