package com.synchronoss.saw.workbench.service;


import org.apache.livy.Job;
import org.apache.livy.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.xdf.context.NGContext;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.parser.NGParser;
import sncr.xdf.sql.ng.NGSQLComponent;
import sncr.xdf.transformer.ng.NGTransformerComponent;
import sncr.xdf.ngcomponent.util.NGComponentUtil;
import java.util.Optional;

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
    AbstractComponent aac = null;
    Exception exception = null;
    int rc = 0;
    try {
        switch (ngctx.componentName) {
            case "sql":
                aac = new NGSQLComponent(ngctx);
                break;
            case "parser":
                aac = new NGParser(ngctx);
                break;
            case "transformer":
                aac = new NGTransformerComponent(ngctx);
                break;
            default:
                throw new IllegalArgumentException("Unknown component: " + ngctx.componentName);
        }
        if (aac.initComponent(jobContext.sc())) {
            log.info("Starting Workbench job");
            rc = aac.run();
            log.info("Workbench job completed, result: " + rc + " error: " + aac.getError());
        }else{
            log.error("Could not initialize component");
            throw new RuntimeException("Could not initialize component:");
        }
    }catch (Exception ex) {
        exception = ex;
    }
    rc = NGComponentUtil.handleErrors(Optional.ofNullable(aac), rc, exception);
    if (rc != 0) {
          throw new RuntimeException("XDF returned non-zero status: " + rc);
      }
    return rc;
  }
}
