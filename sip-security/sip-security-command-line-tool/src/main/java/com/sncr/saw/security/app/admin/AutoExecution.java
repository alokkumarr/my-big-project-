package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.service.AutoExecutionService;
import com.sncr.saw.security.common.bean.Valid;
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

  @ShellMethod("Make Charts,Pivots and ES Reports Execute each time when land on View Analysis Page")
  public String autoExecution(
      @ShellOption(value = "--C", help = "Customer-Code") String customerCode,
      @ShellOption(value = "--F", help = "Active Status Indicator flag", defaultValue = "1") String activeStatusInd) {
    if (StringUtils.isEmpty(customerCode) || customerCode.equalsIgnoreCase("_NONE_")) {
      logger.error("Missing argument!! Use 'help command to print usage!!");
      throw new IllegalArgumentException("Missing argument!! Use 'help command to print usage!!");
    }
    activeStatusInd = activeStatusInd == null ? "1" : activeStatusInd;
    if (!activeStatusInd.matches("0|1")) {
      logger.error("Invalid Active status indicator Flag !! Acceptable Format = [0|1] ");
      return ("Invalid Active status indicator Flag !! Acceptable Format = [0|1] ");
    }
    ConfigValDetails configValDetails = new ConfigValDetails();
    configValDetails.setConfigValCode("es-analysis-auto-refresh");
    configValDetails.setActiveStatusInd(Integer.parseInt(activeStatusInd));
    configValDetails.setConfigValDesc(
        "Make Charts,Pivots and ES Reports Execute each time when land on View Analysis Page");
    configValDetails.setConfigValObjType("CUSTOMER");
    configValDetails.setConfigValObjGroup(customerCode);
    configValDetails.setCreatedBy("saw-admin");
    try {
      Valid valid = autoExecutionService.addConfigVal(configValDetails);
      return valid.getValidityMessage();
    } catch (IllegalArgumentException ie) {
      logger.error("IllegalArgumentException : {}", ie.getMessage());
      return ie.getLocalizedMessage();
    } catch (Exception e) {
      logger.error("Error inserting auto execution : {}", e.getMessage());
      return e.getLocalizedMessage();
    }
  }
}
