package com.synchronoss.saw.workbench.service;

import com.synchronoss.saw.workbench.model.Project;

public interface SAWWorkbenchService {
  
  public Project readDirectoriesByProjectId(Project project) throws Exception;
  public Project createDirectoryProjectId(Project project)throws Exception;
  public Project uploadFilesDirectoryProjectId(Project project)throws Exception;
  public Project previewFromProjectDirectoybyId(Project project)throws Exception;
  public Project inspectFromProjectDirectoybyId(Project project)throws Exception;
}
