package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.service.AutoExecutionService;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

/**
 * @author pras0004
 * @since 3.5.0
 */
@ShellComponent
public class AutoExecution {

  private static final Logger logger = LoggerFactory.getLogger(AutoExecution.class);
  @Autowired
  AutoExecutionService autoExecutionService;

  @ShellMethod("Adds auto execution configuration")
  public String autoExecution(
      @ShellOption(value = "--C", help = "Customer-Code", defaultValue = "_NONE_") String customerCode,
      @ShellOption(value = "--F", help = "Active Status Indicator flag", defaultValue = "_NONE_") String activeStatusInd) {
    if (StringUtils.isEmpty(customerCode) || customerCode.equalsIgnoreCase("_NONE_")) {
      throw new IllegalArgumentException("Missing argument!! Use 'help command to print usage!!");
    }
    if (!StringUtils.isEmpty(activeStatusInd) && !activeStatusInd.matches("0|1")) {
      return ("Invalid Active status indicator Flag !! Acceptable Format = [0|1] ");
    }
    activeStatusInd = activeStatusInd == null ? "1" : activeStatusInd;
    ConfigValDetails configValDetails = new ConfigValDetails();
    configValDetails.setConfigValCode("es-analysis-auto-refresh");
    configValDetails.setActiveStatusInd(Integer.parseInt(activeStatusInd));
    configValDetails.setConfigValDesc(
        "Make Charts,Pivots and ES Reports Execute each time when land on View Analysis Page");
    configValDetails.setConfigValObjType("CUSTOMER");
    configValDetails.setConfigValObjGroup(customerCode);
    configValDetails.setCreatedBy("saw-admin");
    try {
      autoExecutionService.addConfigVal(configValDetails);
      return "Successfully added!!";
    } catch (IllegalArgumentException ie) {
      logger.error("IllegalArgumentException : {}", ie.getMessage());
      return ie.getLocalizedMessage();
    } catch (Exception e) {
      logger.error("Exception while inserting auto execution : {}", e.getMessage());
      return e.getLocalizedMessage();
    }
  }
}
