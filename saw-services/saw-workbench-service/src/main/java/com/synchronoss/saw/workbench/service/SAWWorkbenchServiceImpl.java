package com.synchronoss.saw.workbench.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.synchronoss.saw.workbench.model.Project;

@Service
class SAWWorkbenchServiceImpl implements SAWWorkbenchService {
  
  private static final Logger logger = LoggerFactory.getLogger(SAWWorkbenchServiceImpl.class);
  
  
  @Value("${workbench.project-key}")
  private String defaultProjectId;

  @Value("${workbench.project-path}")
  private String defaultProjectPath;
  
  @Value("${workbench.project-root}")
  private String defaultProjectRoot;
  

  @Override
  public Project readDirectoriesByProjectId(Project project) throws Exception {
    
    return null;
  }

  @Override
  public Project createDirectoryProjectId(Project project) throws Exception {
    
    return null;
  }

  @Override
  public Project uploadFilesDirectoryProjectId(Project project) throws Exception {
   
    return null;
  }

  @Override
  public Project previewFromProjectDirectoybyId(Project project) throws Exception {
    
    return null;
  }

  @Override
  public Project inspectFromProjectDirectoybyId(Project project) throws Exception {
    
    return null;
  }

}
