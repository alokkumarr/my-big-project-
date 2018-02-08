package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.io.FilenameFilter;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.inspect.SAWDelimitedInspector;
import com.synchronoss.saw.inspect.SAWDelimitedReader;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import sncr.bda.core.file.HFileOperations;

@Service
public class SAWWorkbenchServiceImpl implements SAWWorkbenchService {
  
  private static final Logger logger = LoggerFactory.getLogger(SAWWorkbenchServiceImpl.class);
  
  @Value("${workbench.project-key}")
  @NotNull
  private String defaultProjectId;

  @Value("${workbench.project-path}")
  @NotNull
  private String defaultProjectPath;
  
  @Value("${workbench.project-root}")
  @NotNull
  private String defaultProjectRoot;
  
  @Value("${workbench.preview-limit}")
  @NotNull
  private String defaultPreviewLimit;
  
  private String prefix = "maprfs";
  
  @PostConstruct
  private void init() throws Exception {
    if (defaultProjectRoot.startsWith(prefix)) {
      if (!HFileOperations.exists(defaultProjectRoot)) {
        logger.trace("Path {}", defaultProjectRoot + defaultProjectPath);
        HFileOperations.createDir(defaultProjectRoot);
        if (!HFileOperations.exists(defaultProjectRoot + defaultProjectPath)) {
          logger.trace("Path {}", defaultProjectRoot + defaultProjectPath);
          HFileOperations.createDir(defaultProjectRoot + defaultProjectPath);
        }
      }
    } else {
      File directory = new File(defaultProjectRoot);
      if (!directory.exists()) {
        if (directory.mkdirs()) {
          File file = new File(defaultProjectRoot + defaultProjectPath);
          if (!file.exists())
            file.mkdirs();
        }
      }
    }
  }

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
    logger.trace("Previewing a file from {}" + project.getPath());
    String filePath =null;
    String resultJSON =null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    SAWDelimitedReader  sawDelimitedReader = null;
    if (defaultProjectRoot.startsWith(prefix)){
      filePath = defaultProjectRoot + Path.SEPARATOR + defaultProjectPath + Path.SEPARATOR + project.getPath();
      logger.trace("filePath when filesystem is in distributed mode {}", filePath);
        sawDelimitedReader = new SAWDelimitedReader(filePath, Long.parseLong(defaultPreviewLimit), false);
        sawDelimitedReader.parseSomeLines();
        resultJSON = sawDelimitedReader.toJson();
      logger.trace("resutlJSON from preview service {}", resultJSON);
      Inspect inspect = objectMapper.readValue(resultJSON, Inspect.class);
      project.setData(inspect.getSamples());
    }
    else {
      filePath = defaultProjectRoot + defaultProjectPath + File.separator + project.getPath();
      logger.trace("filePath when filesystem is not in distributed mode {}", filePath);
        sawDelimitedReader = new SAWDelimitedReader(filePath, Long.parseLong(defaultPreviewLimit), true);
        sawDelimitedReader.parseSomeLines();
        resultJSON = sawDelimitedReader.toJson();
      logger.trace("resutlJSON from preview service {}", resultJSON);
      Inspect inspect = objectMapper.readValue(resultJSON, Inspect.class);
      project.setData(inspect.getSamples());
    }
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }

  @Override
  public Inspect inspectFromProjectDirectoybyId(Inspect inspect) throws Exception {
    logger.trace("Inspecting a file from {}" + inspect.getFile());
    String filePath =null;
    String resultJSON =null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String inspectJSON = "{ \"inspect\" :" + objectMapper.writeValueAsString(inspect) + "}";
    logger.trace("InspectJSON {}", inspectJSON);
    SAWDelimitedInspector  sawDelimitedInspector = null;
    if (defaultProjectRoot.startsWith(prefix)){
      filePath = defaultProjectRoot + Path.SEPARATOR + defaultProjectPath + Path.SEPARATOR + inspect.getFile();
      logger.trace("filePath when filesystem is in distributed mode {}", filePath);
        sawDelimitedInspector = new SAWDelimitedInspector(inspectJSON, filePath, false);
        sawDelimitedInspector.parseSomeLines();
        resultJSON = sawDelimitedInspector.toJson();
      logger.trace("resutlJSON from inspect service {}", resultJSON);
      inspect = objectMapper.readValue(resultJSON, Inspect.class);
      
    }
    else {
      filePath = defaultProjectRoot + defaultProjectPath + File.separator + inspect.getFile();
      logger.trace("filePath when filesystem is not in distributed mode {}", filePath);
        sawDelimitedInspector = new SAWDelimitedInspector(inspectJSON, filePath, true);
        sawDelimitedInspector.parseSomeLines();
        resultJSON = sawDelimitedInspector.toJson();
      logger.trace("resutlJSON from inspect service {}", resultJSON);
      inspect = objectMapper.readValue(resultJSON, Inspect.class);
      
    }
    logger.trace("response structure {}", objectMapper.writeValueAsString(inspect));
    return inspect; }
  
  public static void main(String[] args) {
    String path = "/Users/spau0004/Desktop/*.csv";
    File file = new File(path);
    System.out.println(file.isDirectory());
    System.out.println(file.isFile());
    System.out.println(file.getAbsolutePath());
    String extension = FilenameUtils.getExtension(path);
    String basePath = FilenameUtils.getFullPathNoEndSeparator(path);
    System.out.println(extension);
    System.out.println(basePath);
    File dir = new File(basePath);
    final FilenameFilter filter = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
          return name.endsWith(extension);
      }
  };
  System.out.println(dir.listFiles(filter)[0].getAbsolutePath());
  }
}
