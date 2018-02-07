package com.synchronoss.saw.workbench.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.workbench.AsyncConfiguration;
import com.synchronoss.saw.workbench.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import com.synchronoss.saw.workbench.service.SAWWorkbenchService;

/**
 * @author spau0004
 * This class is used to perform Workbench operation<br/>
 * which is internal to the service <br/>
 */
@RestController
@RequestMapping("/internal/workbench/projects/")
public class SAWWorkBenchInternalAddRAWDataController {

  private static final Logger logger = LoggerFactory.getLogger(SAWWorkBenchInternalAddRAWDataController.class);

  @Value("${workbench.project-key}")
  private String defaultProjectId;

  @Value("${workbench.project-path}")
  @NotNull
  private String defaultProjectPath;
  
  @Value("${workbench.project-root}")
  @NotNull
  private String defaultProjectRoot;
  
 @Autowired
 private SAWWorkbenchService sawWorkbenchService;
  
   /**
   * @return {@link Project}
   * @throws JsonProcessingException
   */
  
  @RequestMapping(value = "list", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
    public List<Project> retrieveProjects() throws JsonProcessingException {
      logger.debug("Retrieve Default Project");
      List<Project> projects = new ArrayList<Project>();
      Project project = new Project();
      project.setPath(defaultProjectRoot + defaultProjectPath);
      project.setProjectId(defaultProjectId);
      projects.add(project);
      return projects;
    }
  @RequestMapping(value = "raw/default", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
    public Project retrieveProject() throws JsonProcessingException {
      logger.debug("Retrieve Default Project");
      Project project = new Project();
      project.setPath(defaultProjectRoot + defaultProjectPath);
      project.setProjectId(defaultProjectId);
      return project;
    }
 
  /**
   * @param projectId
   * @param request
   * @param response
   * @return
   * @throws JsonProcessingException
   */
  @RequestMapping(value = "{projectId}/raw", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Project retrieveProjectDirectoriesDetailsById(@PathVariable(name = "projectId", required = true) String projectId, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id ", projectId);
    Project project = new Project();
    project.setProjectId(projectId);
    return project;
  }  


  /**
   * @param projectId
   * @param relativePath
   * @param request
   * @param response
   * @return
   * @throws JsonProcessingException
   */
  @RequestMapping(value = "{projectId}/raw/directory", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Project retrieveProjectDirectoriesDetailsByIdAndDirectoryPath(@PathVariable(name = "projectId", required = true) String projectId, 
      @RequestParam(name = "path", required = true) String relativePath, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id ", projectId);
    Project project = new Project();
    project.setProjectId(projectId);
    project.setPath(relativePath);
    return project;
  }  

  /**
   * @param projectId
   * @param relativePath
   * @param request
   * @param response
   * @return
   * @throws JsonProcessingException
   */
  @RequestMapping(value = "{projectId}/raw/directory/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Project createProjectDirectoryDetailsByIdAndDirectoryPath(@RequestBody Project project) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id ", project.getProjectId());
    return project;
  } 
  
  /**
   * @param projectId
   * @param relativePath
   * @param request
   * @param response
   * @return
   * @throws JsonProcessingException
   */
  @RequestMapping(value = "{projectId}/raw/directory/upload/files", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Project uploadFilesToProjectDirectoryByIdAndInDirectoryPath(@RequestBody Project project) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id ", project.getProjectId());
    return project;
  } 

  @RequestMapping(value = "{projectId}/raw/directory/preview", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Project previewRawDatafromProjectDirectoybyId(@PathVariable(name = "projectId", required = true) String projectId, 
      @RequestBody Project project) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id {} ", projectId);
    Preconditions.checkNotNull(project.getPath(), "To preview a file path attribute cannot be null");
    Project responseProject = null;
    try {
      responseProject = sawWorkbenchService.previewFromProjectDirectoybyId(project);
    } catch (Exception e) {
      logger.error("Exception occured while previewing the raw data", e);
      throw new ReadEntitySAWException("Exception occured while previewing the raw data", e);
    }
    project.setProjectId(projectId);
    return responseProject;
  } 

  @RequestMapping(value = "{projectId}/raw/directory/inspect", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Inspect inspectRawDatafromProjectDirectoybyId(@PathVariable(name = "projectId", required = true) String projectId, 
      @RequestBody Inspect inspect) throws JsonProcessingException {
    logger.debug("Retrieve project details By Id {} ", projectId);
    Preconditions.checkNotNull(inspect.getFile(), "To preview a file attribute cannot be null");
    Inspect responseInspect = null;
    try {
      responseInspect = sawWorkbenchService.inspectFromProjectDirectoybyId(inspect);
    } catch (Exception e) {
      logger.error("Exception occured while inspecting the raw data", e);
      throw new ReadEntitySAWException("Exception occured while inspecting the raw data", e);
    }
    return responseInspect;
  } 
  
  /**
   * This method is used to get the data based on the storage type<br/>
   * perform conversion based on the specification asynchronously
   * @param Id
   * @param request
   * @param response
   * @param requestBody
   * @return
   */
  @Async(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
  @RequestMapping(value = "/projects/{projectId}/raw/async", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public CompletableFuture<Project> retrieveStorageDataAsync(@PathVariable(name = "projectId", required = true) String projectId, HttpServletRequest request, HttpServletResponse response) {
    CompletableFuture<Project> responseObjectFuture = null;
   try {
     responseObjectFuture= CompletableFuture.
         supplyAsync(() -> {
          Project proxyResponseData = null; 
            try {
              //proxyResponseData = proxyService.execute(proxyNode);
            }catch (Exception e) {
              logger.error("Exception generated while processing incoming json.", e);
              //proxyResponseData= SAWWorkBenchUtils.prepareResponse(proxyNode.getProxy(), e.getCause().toString());
            }
        return proxyResponseData;
         })
         .handle((res, ex) -> {
           if(ex != null) {
               logger.error("While retrieving data there is an exception.", ex);
               res.setStatusMessage(ex.getCause().toString());
               return res;
           }
           return res;
       });
    }  catch (ReadEntitySAWException ex) {
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } 
   return responseObjectFuture;
  }
}
  
  
