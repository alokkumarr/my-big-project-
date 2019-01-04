package com.synchronoss.saw.workbench.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.mapr.db.MapRDB;
import com.synchronoss.saw.inspect.SAWDelimitedInspector;
import com.synchronoss.saw.inspect.SAWDelimitedReader;
import com.synchronoss.saw.workbench.AsyncConfiguration;
import com.synchronoss.saw.workbench.SAWWorkBenchUtils;
import com.synchronoss.saw.workbench.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.workbench.model.DataSet;
import com.synchronoss.saw.workbench.model.Inspect;
import com.synchronoss.saw.workbench.model.Project;
import com.synchronoss.saw.workbench.model.Project.ResultFormat;
import com.synchronoss.saw.workbench.model.StorageProxy;
import com.synchronoss.saw.workbench.model.StorageProxy.Storage;
import com.synchronoss.saw.workbench.model.StorageType;
import sncr.bda.base.MetadataBase;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.metastore.DataSetStore;
import sncr.bda.metastore.ProjectStore;
import sncr.bda.services.DLMetadata;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;

@Service
public class SAWWorkbenchServiceImpl implements SAWWorkbenchService {

  private static final Logger logger = LoggerFactory.getLogger(SAWWorkbenchServiceImpl.class);

  private static final String[] METADATA_TABLES = {
      "auditlog", "datapods", "datasegments", "datasets", "projects",
      "transformations"
  };

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

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${workbench.storage-uri}")
  @NotNull
  private String storageURL;

  private String tmpDir = null;
  private DLMetadata mdt = null;
  private DataSetStore mdtStore =null;
  private String prefix = "maprfs";
  private String delimiter = "::";
  private String dateFormat = "yyyy-mm-dd hh:mm:ss";
  @Autowired
  private WorkbenchExecutionService workbenchExecutionService;

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


    HFileOperations.createDir(defaultProjectRoot);
    for (String table : METADATA_TABLES) {
        createMetadataTable(table);
    }

    ProjectStore ps = new ProjectStore(defaultProjectRoot);
    try {
        ps.readProjectData(defaultProjectId);
    } catch (Exception e) {
        logger.info("Creating default project: {}", defaultProjectId);
        ps.createProjectRecord(defaultProjectId, "{}");
    }

    if (defaultProjectRoot.startsWith(prefix)) {
     logger.trace("Initializing defaultProjectRoot {}", defaultProjectRoot);
    this.mdt = new DLMetadata(defaultProjectRoot);
    this.mdtStore = new DataSetStore(basePath);}
    this.tmpDir = System.getProperty("java.io.tmpdir");
  }

  private void createMetadataTable(String table) {
    String path = defaultProjectRoot + "/services/metadata/" + table;
    if(!MapRDB.tableExists(path)) {
        logger.info("Creating metadata table: {}" + path);
        MapRDB.createTable(path);
    }
  }

  @Override
  public Project readDirectoriesByProjectId(Project project, String relativePath) throws Exception {
    logger.trace("readDirectoriesByProjectId : Reading data from the root {}",  this.mdt.getRoot());
    logger.trace("readDirectoriesByProjectId :Reading data from {} " , project.getPath());
    List<String> directories = this.mdt.getListOfStagedFiles(project.getPath(), null, relativePath);
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
    logger.trace("readSubDirectoriesByProjectId : Reading data from the root {}",  this.mdt.getRoot());
    logger.trace("readSubDirectoriesByProjectId:  Reading data from {}",  defaultProjectPath + project.getPath());
    List<String> directories = this.mdt.getListOfStagedFiles(defaultProjectRoot + defaultProjectPath, project.getPath(), project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    List<Object> data = new ArrayList<>();
    for (String directory : directories) {
      logger.trace("Reading data in readSubDirectoriesByProjectId {}" + directory);
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
    Project readProject = new Project();
    String holdTempPathValue = project.getPath();
    if (!HFileOperations.exists(defaultProjectRoot + defaultProjectPath + project.getPath())) {
      logger.trace("Path {}", defaultProjectRoot + defaultProjectPath + project.getPath());
      HFileOperations.createDir(defaultProjectRoot + defaultProjectPath + project.getPath());
      String pathForSubdirectory = FilenameUtils.getFullPathNoEndSeparator(project.getPath());
      logger.trace("After creating folder before reading in createDirectoryProjectId {}", objectMapper.writeValueAsString(project));
      logger.trace("pathForSubdirectory {}", pathForSubdirectory);
      project.setPath(pathForSubdirectory);
      readProject = readSubDirectoriesByProjectId(project);
      logger.trace("readSubDirectoriesByProjectId(project) {}", project.getPath());
      logger.trace("After creating folder after reading in createDirectoryProjectId {}", objectMapper.writeValueAsString(readProject));
      project.setData(readProject.getData());
    }
    project.setPath(holdTempPathValue);
    project.setStatusMessage(project.getPath() + " has been created successfully.");
    logger.trace("response structure {}", objectMapper.writeValueAsString(project));
    return project;
  }

  @Override
  public Project uploadFilesDirectoryProjectId(Project project, List<File> uploadfiles) throws Exception {
    logger.trace("uploading multiple files the data directory {}", project.getPath());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    project.setResultFormat(ResultFormat.JSON);
    String projectPath = defaultProjectPath + project.getPath();
    int success =0;
    StringBuilder filebuilder = new StringBuilder();
    for (File file : uploadfiles){
      if (file.isDirectory()){
        throw new IOException("It is directory which cannot be uploaded");
      }
      logger.trace("file name {}", file.getName());
      filebuilder.append(file.getName());
      filebuilder.append(": ");
      String absolutePath = file.getAbsolutePath();
      logger.trace("file name absolutePath {}", absolutePath);
      success = this.mdt.moveToRaw(projectPath, absolutePath, null, file.getName());
      if (success!=0){
        throw new IOException("While copying file " + file.getName() + " from " + absolutePath + " to " + projectPath);
      }
    }
    Project readProject = readSubDirectoriesByProjectId(project);
    project.setData(readProject.getData());
    project.setStatusMessage("Files are successfully uploaded - "+ filebuilder.toString() + " Status :" +HttpStatus.OK);
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
        resultJSON = sawDelimitedReader.toJson();
      logger.trace("resutlJSON from preview service {}", resultJSON);
      Inspect inspect = objectMapper.readValue(resultJSON, Inspect.class);
      project.setData(inspect.getSamples());
    }
    else {
      filePath = defaultProjectRoot + defaultProjectPath + File.separator + project.getPath();
      logger.trace("filePath when filesystem is not in distributed mode {}", filePath);
        sawDelimitedReader = new SAWDelimitedReader(filePath, Long.parseLong(defaultPreviewLimit), true);
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
        sawDelimitedInspector = new SAWDelimitedInspector(inspectJSON, filePath, false,inspect.getFile());
        sawDelimitedInspector.parseSomeLines();
        resultJSON = sawDelimitedInspector.toJson();
      logger.trace("resutlJSON from inspect service {}", resultJSON);
      inspect = objectMapper.readValue(resultJSON, Inspect.class);

    }
    else {
      filePath = defaultProjectRoot + defaultProjectPath + File.separator + inspect.getFile();
      logger.trace("filePath when filesystem is not in distributed mode {}", filePath);
        sawDelimitedInspector = new SAWDelimitedInspector(inspectJSON, filePath, true,inspect.getFile());
        sawDelimitedInspector.parseSomeLines();
        resultJSON = sawDelimitedInspector.toJson();
      logger.trace("resutlJSON from inspect service {}", resultJSON);
      inspect = objectMapper.readValue(resultJSON, Inspect.class);

    }
    logger.trace("response structure {}", objectMapper.writeValueAsString(inspect));
    return inspect; }

  @Override
  public List<DataSet> listOfDataSet(Project project) throws Exception {
    logger.trace("Getting a dataset for the project {} " + project.getProjectId());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    List<String> dataSetsString = mdtStore.getListOfDS(project.getProjectId(), null, null, null, null);
    List<DataSet> dataSetsJSON = new ArrayList<>();
    DataSet dataSet = null;
    for (String item : dataSetsString){
      logger.trace("item from datasets store {} ", item);
      dataSet = objectMapper.readValue(item, DataSet.class);
      dataSet.setJoinEligible(true);
      dataSet.setStorageType(StorageType.DL.name());
      dataSetsJSON.add(dataSet);
    }
    //dataSetsJSON.addAll(listOfDataSetAvailableInESStore(project));
    logger.trace("response structure {}", objectMapper.writeValueAsString(dataSetsJSON));
    return dataSetsJSON;
  }
  /**
   * This method generates the structure aligned to meta data store structure
   * @param project
   * @return List<DataSet>
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws JsonProcessingException
   * @throws IOException
   */
  private List<DataSet> listOfDataSetAvailableInESStore (Project project) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException{
    logger.trace("listOfDataSetFromESStore  starts here : ", project);
    List <DataSet>  dataSets = new ArrayList<>();
    String storageEndpoints = "/internal/proxy/storage";
    String url = storageURL + storageEndpoints;
    StorageProxy storageProxy = new StorageProxy();
    storageProxy.setStorage(Storage.ES);
    storageProxy.setRequestBy("workbenchAdmin@synchronoss.com");
    storageProxy.setAction(com.synchronoss.saw.workbench.model.StorageProxy.Action.CATALIASES);
    storageProxy.setRequestedTime(new SimpleDateFormat(dateFormat).format(new Date()));
    storageProxy.setProductCode("WORKBENCH");
    storageProxy.setModuleName("WORKBENCH");
    storageProxy.setResultFormat(com.synchronoss.saw.workbench.model.StorageProxy.ResultFormat.JSON);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String jsonInString = null;
    try {
      jsonInString = objectMapper.writeValueAsString(storageProxy);
      logger.trace("jsonInString :" + jsonInString);
    } catch (JsonProcessingException e) {
        throw new ReadEntitySAWException("Exception occured while reading aliases from Elastic Search");
    }
    HttpHeaders  requestHeaders = new HttpHeaders();
    requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<Object>(jsonInString,requestHeaders);
    RestTemplate restTemplate = new RestTemplate();
    logger.debug("storageURL server URL {}", url);
    ResponseEntity<StorageProxy> aliasResponse = restTemplate.exchange(url, HttpMethod.POST,
        requestEntity, StorageProxy.class);
    Preconditions.checkNotNull(aliasResponse.getBody().getData()!=null, "Alias data set from saw storage as null");
    List<Object> listOfAliases = aliasResponse.getBody().getData();
    DataSet esDataSet = null;
    String jsonInStringPerAlias = null;
    ResponseEntity<StorageProxy> perAliasResponse = null;
    HttpHeaders  requestHeadersPerAlias = null;
    requestHeadersPerAlias = new HttpHeaders();
    requestHeadersPerAlias.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
    StorageProxy proxyDataFromStorage = null;
    for (Object aliaseName : listOfAliases) {
      proxyDataFromStorage = new StorageProxy();
      proxyDataFromStorage.setStorage(Storage.ES);
      proxyDataFromStorage.setRequestBy("workbenchAdmin@synchronoss.com");
      proxyDataFromStorage.setAction(com.synchronoss.saw.workbench.model.StorageProxy.Action.CATALIASES);
      proxyDataFromStorage.setRequestedTime(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
      proxyDataFromStorage.setProductCode("WORKBENCH");
      proxyDataFromStorage.setModuleName("WORKBENCH");
      String name = (String) aliaseName;
      System.out.println("aliaseName : " + name);
      proxyDataFromStorage.setAction(com.synchronoss.saw.workbench.model.StorageProxy.Action.MAPPINGALIASES);
      proxyDataFromStorage.setIndexName(name);
      try {
        jsonInStringPerAlias = objectMapper.writeValueAsString(proxyDataFromStorage);
        logger.trace("jsonInStringPerAlias : " + jsonInStringPerAlias);
      } catch (JsonProcessingException e) {
          throw new ReadEntitySAWException("Exception occured while reading aliases from Elastic Search");
      }
      HttpEntity<?> requestEntityPerAlias = new HttpEntity<Object>(jsonInStringPerAlias,requestHeadersPerAlias);
      perAliasResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntityPerAlias, StorageProxy.class);
      proxyDataFromStorage = perAliasResponse.getBody();
      List<Object> data = proxyDataFromStorage.getData();
      String id = UUID.randomUUID().toString() + delimiter + "esData" + delimiter + System.currentTimeMillis();
      ObjectNode node = JsonNodeFactory.instance.objectNode();
      node.put("_id", id);
      ObjectNode system = node.putObject(DataSetProperties.System.toString());
      system.put(DataSetProperties.Name.toString(), name);
      system.put(DataSetProperties.Catalog.toString(), MetadataBase.DEFAULT_CATALOG);
      system.put("project", "workbench");
      system.put(DataSetProperties.Format.toString(), "json");
      system.put(DataSetProperties.PhysicalLocation.toString(), perAliasResponse.getBody().getIndexRelativePath());
      ObjectNode userData = node.putObject(DataSetProperties.UserData.toString());
      userData.put(DataSetProperties.Description.toString(), "Data Structure for "+ name);
      userData.put("component", "esData");
      userData.put(DataSetProperties.createdBy.toString(), "workbenchAdmin@synchronoss.com");
      ObjectNode asOfNow = node.putObject("asOfNow");
      asOfNow.put(DataSetProperties.Status.toString(), "SUCCESS");
      asOfNow.put(DataSetProperties.StartTS.toString(), new SimpleDateFormat(dateFormat).format(new Date()));
      asOfNow.put(DataSetProperties.FinishTS.toString(), new SimpleDateFormat(dateFormat).format(new Date()));
      ObjectNode schema = node.putObject(DataSetProperties.Schema.toString());
      ArrayNode fields = JsonNodeFactory.instance.arrayNode();
      for (Object obj : data) {
        fields.addPOJO(obj);
      } // end of internal for loop to read storeField
      schema.putArray("fields").addAll(fields);
      esDataSet = objectMapper.readValue(objectMapper.writeValueAsString(node), DataSet.class);
      esDataSet.setStorageType(StorageType.ES.name());
      esDataSet.setJoinEligible(false);
      esDataSet.setRecordCount(Long.parseLong(perAliasResponse.getBody().getCount()));
      esDataSet.setSize(perAliasResponse.getBody().getSize());
      dataSets.add(esDataSet);
    } // end of for loop
    logger.trace("listOfDataSetFromESStore  ends here : ", dataSets);
    return dataSets;
  }

  public DataSet getDataSet(String projectId, String datasetName) throws Exception {
    logger.trace("Getting dataset properties for the project {} and dataset {}", projectId, datasetName);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    String dataset = mdtStore.readDataSet(projectId, datasetName);
    return objectMapper.readValue(dataset, DataSet.class);
  }
  
  //TODO: This method needs re-factoring in future once SIP-4218 & SIP-4217 is resolved
  @Override
  public DataSet createDataSet(DataSet dataSet, String project) throws Exception {
    logger.trace("createDataSet starts here :  " + dataSet.toString());
    MetaDataStoreRequestAPI requestMetaDataStore = null;
    String category = Category.DataSet.name();
    String format = "parquet";
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(dataSet));
    ObjectNode rootNode = (ObjectNode) node;
    Preconditions.checkNotNull(rootNode.get("asInput"), "asInput cannot be null");
    Preconditions.checkNotNull(rootNode.get("asOfNow"), "asOfNow cannot be null");
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.RecordCount.toString()), "recordCount cannot be null");
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.UserData.toString()).get(DataSetProperties.Component.toString()),
        "userData.component cannot be null");
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.UserData.toString())
        .get(DataSetProperties.createdBy.toString()), "userData.createdBy cannot be null");
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.UserData.toString()).get("script"),
        "userdata.script cannot be null");
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.System.toString()).get("name"),
        "system.name cannot be null");
    Preconditions.checkNotNull(rootNode.get("asOfNow").get("status"),
        "asOfNow.status cannot be null");
    Preconditions.checkNotNull(rootNode.get("asOfNow").get("started"),
        "asOfNow.started cannot be null");
    Preconditions.checkNotNull(rootNode.get("asOfNow").get("finished"),
        "asOfNow.finished cannot be null");
    Preconditions.checkNotNull(rootNode.get("asOfNow").get("batchId"),
        "asOfNow.batchId cannot be null");
    String id = project + delimiter + rootNode.get(DataSetProperties.System.toString()).get(DataSetProperties.Name.toString()).asText();
    rootNode.put(DataSetProperties.Id.toString(), id);
    ObjectNode transformationNode =
        rootNode.putObject(DataSetProperties.Transformations.toString());
    transformationNode.put("asOutput", id);
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.UserData.toString()), "userData cannot be null");
    ObjectNode userDataNode = (ObjectNode) rootNode.get(DataSetProperties.UserData.toString());
    userDataNode.put(DataSetProperties.Category.toString(), category);
    userDataNode.put(DataSetProperties.Component.toString(), dataSet.getComponent());
    userDataNode.put(DataSetProperties.ScriptLocation.toString(), rootNode.get(DataSetProperties.UserData.toString()).get(DataSetProperties.Script.toString()).toString());
    Preconditions.checkNotNull(rootNode.get(DataSetProperties.System.toString()), "system cannot be null");
    ObjectNode systemNode = (ObjectNode) rootNode.get(DataSetProperties.System.toString());
    String catalog = systemNode.get(DataSetProperties.Catalog.toString()) != null
        ? systemNode.get(DataSetProperties.Catalog.toString()).textValue()
        : MetadataBase.DEFAULT_CATALOG;
    systemNode.put(DataSetProperties.Project.toString(), project);
    systemNode.put(DataSetProperties.Format.toString(),
    systemNode.get(DataSetProperties.Format.toString()) != null
            ? systemNode.get(DataSetProperties.Format.toString()).toString()
            : format);
     systemNode.put(DataSetProperties.PhysicalLocation.toString(),workbenchExecutionService.createDatasetDirectory(project,catalog,
            rootNode.get(DataSetProperties.System.toString()).get(DataSetProperties.Name.toString()).textValue()));
    systemNode.put(DataSetProperties.Catalog.toString(),catalog);
    //ArrayNode inputPath = objectMapper.createArrayNode();
    //inputPath.addAll((ArrayNode) rootNode.get(DataSetProperties.System.toString()).get("inputPath"));
    //systemNode.putArray("inputPath").addAll(inputPath);
    DataSet dataSetNode = objectMapper.readValue(node.toString(), DataSet.class);
    try {
      List<MetaDataStoreStructure> structure = SAWWorkBenchUtils.node2JSONObject(dataSetNode,
          basePath, id, Action.create, Category.DataSet);
      logger.trace("Before invoking request to MaprDB JSON store :{}",
          objectMapper.writeValueAsString(structure));
      requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      List<MetaDataStoreStructure> structureRead = SAWWorkBenchUtils.node2JSONObject(dataSetNode,
          basePath, id, Action.read, Category.DataSet);
      requestMetaDataStore = new MetaDataStoreRequestAPI(structureRead);
      requestMetaDataStore.process();
      String jsonStringFromStore = requestMetaDataStore.getResult().toString();
      ObjectMapper mapper = new ObjectMapper();
      dataSet = mapper.readValue(jsonStringFromStore, DataSet.class);
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Problem on the storage while creating an entity", ex);
    }
    logger.trace("createEntryInMetaData ends here: " + objectMapper.writeValueAsString(dataSet));
    return dataSet;
  }

  public static void main(String[] args) throws JsonProcessingException, IOException {
    String json = "{\"name\":\"normal.csv\",\"size\":254743,\"d\":false,\"cat\":\"root\"}";
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeJ = objectMapper.readTree(json);
    System.out.println(nodeJ);
    System.out.println(System.getProperty("java.io.tmpdir"));
    System.out.println("data.csv".substring("data.csv".indexOf('.'), "data.csv".length()));
    System.out.println(FilenameUtils.getFullPathNoEndSeparator("/apps/sncr"));
    String row1 = "{\"_id\":\"xda-ux-sr-comp-dev::TRTEST_JEXLREF_SS\",\"system\":{\"user\":\"A_user\",\"project\":\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":\"TRTEST_JEXLREF_SS\",\"physicalLocation\":\"data\",\"catalog\":\"dout\",\"numberOfFiles\":\"1\"},\"userData\":{\"createdBy\":\"S.Ryabov\",\"category\":\"subcat1\",\"description\":\"Transformer component test case: transformed records\"},\"transformations\":[],\"asOutput\":\"xda-ux-sr-comp-dev::transformer::165407713\",\"asOfNow\":{\"status\":\"SUCCESS\",\"started\":\"20180209-195737\",\"finished\":\"20180209-195822\",\"aleId\":\"xda-ux-sr-comp-dev::1518206302595\",\"batchId\":\"BJEXLREFSS\"}}";
    String row2= "{\"_id\":\"xda-ux-sr-comp-dev::tc220_1\",\"system\":{\"user\":\"A_user\",\"project\":\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":\"tc220_1\",\"physicalLocation\":\"hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dout/tc220_1/data\",\"catalog\":\"dout\",\"numberOfFiles\":\"2\"},\"userData\":{\"createdBy\":\"S.Ryabov\",\"category\":\"subcat1\",\"description\":\"SQL component test case\"},\"transformations\":[{\"asOutput\":\"xda-ux-sr-comp-dev::sql::1192296717\"}],\"asOutput\":\"xda-ux-sr-comp-dev::sql::522761969\",\"asOfNow\":{\"status\":\"SUCCESS\",\"started\":\"20180223-220236\",\"finished\":\"20180223-220316\",\"aleId\":\"xda-ux-sr-comp-dev::1519423396639\",\"batchId\":\"BSQL10PM\"}}";
    List<DataSet> sets = new ArrayList<>();
    sets.add(objectMapper.readValue(row1, DataSet.class));
    sets.add(objectMapper.readValue(row2, DataSet.class));
    System.out.println(objectMapper.writeValueAsString(sets));
    DataSet dataset = sets.get(0);
    System.out.println("Dataset : " +dataset.getSystem());
    System.out.println(DataSetProperties.UserData.toString());
  }

}
