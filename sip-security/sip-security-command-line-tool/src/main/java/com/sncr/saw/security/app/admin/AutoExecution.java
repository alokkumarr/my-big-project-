package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.service.AutoExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author pras0004
 * @since 3.5.0
 */
@ShellComponent
public class AutoExecution {

  @Autowired
  AutoExecutionService autoExecutionService;

  @ShellMethod("Adds auto execution configuration")
  public String setAutoExecution(@ShellOption(value = "--C",help = "Customer-Code",defaultValue = "_NONE_") String customerCode,
      @ShellOption(value = "--F",help = "Active Status Indicator flag",defaultValue = "_NONE_") String isJvCustomer) {

    return null;
  }
}
