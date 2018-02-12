package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.inspect.SAWDelimitedInspector;
import com.synchronoss.saw.inspect.SAWDelimitedReader;
import com.synchronoss.saw.workbench.AsyncConfiguration;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import com.synchronoss.saw.workbench.model.Project.ResultFormat;

import sncr.bda.core.file.HFileOperations;
import sncr.bda.services.DLMetadata;

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

  private String tmpDir = null;
  private DLMetadata mdt = null;
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
    if (defaultProjectRoot.startsWith(prefix)) {
    this.mdt = new DLMetadata(defaultProjectRoot);}
    this.tmpDir = System.getProperty("java.io.tmpdir");
  }

  @Override
  public Project readDirectoriesByProjectId(Project project) throws Exception {
    logger.trace("Reading data from {}" + project.getPath());
    List<String> directories = this.mdt.getListOfStagedFiles(defaultProjectPath, null);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    List<Object> data = new ArrayList<>();
    for (String directory : directories) {
      JsonNode node = objectMapper.readTree(directory);
      data.add(node);
    }
    project.setData(data);
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }

  @Override
  public Project readSubDirectoriesByProjectId(Project project) throws Exception {
    logger.trace("Reading data from {}" + project.getPath());
    List<String> directories = this.mdt.getListOfStagedFiles(defaultProjectPath, project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    List<Object> data = new ArrayList<>();
    for (String directory : directories) {
      JsonNode node = objectMapper.readTree(directory);
      data.add(node);
    }
    project.setData(data);
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }
  
  
  @Override
  public Project createDirectoryProjectId(Project project) throws Exception {
    logger.trace("Creating the data directory {}", project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    Project readProject = null;
    if (!HFileOperations.exists(defaultProjectRoot + defaultProjectPath + project.getPath())) {
      logger.trace("Path {}", defaultProjectRoot + defaultProjectPath + project.getPath());
      HFileOperations.createDir(defaultProjectRoot + defaultProjectPath + project.getPath());
      readProject = readSubDirectoriesByProjectId(project);
      project.setData(readProject.getData());
    }
    project.setStatusMessage(project.getPath() + "has been created successfully.");
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }

  @Override
  public Project uploadFilesDirectoryProjectId(Project project, MultipartFile[] uploadfiles) throws Exception {
    logger.trace("uploading multiple files the data directory {}", project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    String projectPath = defaultProjectPath + project.getPath();
    int success =0;
    for (MultipartFile file : uploadfiles){
      if(file.isEmpty()){
        continue;
      }
      byte[] bytes = file.getBytes();
      java.nio.file.Path path = Paths.get(this.tmpDir + file.getOriginalFilename());
      java.nio.file.Path tmpPath = Files.write(path, bytes);
      String absolutePath = tmpPath.toAbsolutePath().toString();
      success = this.mdt.moveToRaw(projectPath, absolutePath, null, file.getOriginalFilename());
      if (success!=0){
        throw new IOException("While copying file " + file.getOriginalFilename() + " from " + tmpPath + " to " + projectPath);
      }
    }
    Project readProject = readSubDirectoriesByProjectId(project);
    project.setData(readProject.getData());
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }
  
  
  @Override
  @Async(AsyncConfiguration.TASK_EXECUTOR_SERVICE)
  public Project uploadFilesDirectoryProjectIdAsync(Project project, MultipartFile[] uploadfiles) throws Exception {
    logger.trace("uploading multiple files the data directory {}", project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    String projectPath = defaultProjectPath + project.getPath();
    int success =0;
    for (MultipartFile file : uploadfiles){
      if(file.isEmpty()){
        continue;
      }
      byte[] bytes = file.getBytes();
      java.nio.file.Path path = Paths.get(this.tmpDir + file.getOriginalFilename());
      java.nio.file.Path tmpPath = Files.write(path, bytes);
      String absolutePath = tmpPath.toAbsolutePath().toString();
      success = this.mdt.moveToRaw(projectPath, absolutePath, null, file.getOriginalFilename());
      if (success!=0){
        throw new IOException("While copying file " + file.getOriginalFilename() + " from " + tmpPath + " to " + projectPath);
      }
    }
    Project readProject = readSubDirectoriesByProjectId(project);
    project.setData(readProject.getData());
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
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
 
  public static void main(String[] args) throws JsonProcessingException, IOException {
    String json = "{\"name\":\"normal.csv\",\"size\":254743,\"d\":false,\"cat\":\"root\"}";
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode node = objectMapper.readTree(json);
    System.out.println(node);
    System.out.println(System.getProperty("java.io.tmpdir"));
    System.out.println("data.csv".substring("data.csv".indexOf('.'), "data.csv".length()));
    
  }
}
