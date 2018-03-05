package com.synchronoss.saw.workbench.service;

public interface WorkbenchExecutionService {
    String execute(String name, String component, String config) throws Exception;
}
