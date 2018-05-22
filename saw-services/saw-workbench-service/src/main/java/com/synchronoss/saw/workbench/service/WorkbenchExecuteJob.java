package com.synchronoss.saw.workbench.service;

import com.cloudera.livy.Job;
import com.cloudera.livy.JobContext;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.xdf.component.Component;
import sncr.xdf.context.Context;
import sncr.xdf.parser.Parser;
import sncr.xdf.sql.TempSqlComponent;

public class WorkbenchExecuteJob implements Job<Integer> {
  private static final long serialVersionUID = 1L;
  private final String root;
  private final String project;
  private final String component;
  private final String config;

  /**
   * This is constructor.
   * @param root is of type String.
   * @param project is of type String.
   * @param component is of type String.
   * @param config is of type String.
   */
  public WorkbenchExecuteJob(String root, String project, String component, String config) {
    this.root = root;
    this.project = project;
    this.component = component;
    this.config = config;
  }
 
  /**
   * This is override method.
   */
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
      xdfComponent = new TempSqlComponent() {
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
      throw new IllegalArgumentException("Unknown component: " + component);
    }
    int status = Component.startComponent(xdfComponent, root, config, project, batch);
    if (status != 0) {
      throw new RuntimeException("XDF returned non-zero status: " + status);
    }
    log.info("Finished execute job");
    return null;
  }
}
