package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.synchronoss.saw.workbench.model.DataSet;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import com.synchronoss.saw.workbench.model.DSSearchParams;

public interface SAWWorkbenchService {
  
  public Project readDirectoriesByProjectId(Project project, String relativePath) throws Exception;
  public Project createDirectoryProjectId(Project project)throws Exception;
  public Project uploadFilesDirectoryProjectId(Project project, List<File> uploadfiles)throws Exception;
  public Project uploadFilesDirectoryProjectIdAsync(Project project, MultipartFile[] uploadfiles)throws Exception;
  public Project previewFromProjectDirectoybyId(Project project)throws Exception;
  public Inspect inspectFromProjectDirectoybyId(Inspect inspect)throws Exception;
  public Project readSubDirectoriesByProjectId(Project project) throws Exception;
  public List<DataSet> listOfDataSet(Project project, Optional<DSSearchParams> optionalSearchParams) throws Exception;
  public DataSet getDataSet(String projectId, String datasetId) throws Exception;
  public DataSet createDataSet(DataSet dataSet, String proj) throws Exception;

}
