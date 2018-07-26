package sncr.xdf.component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import sncr.bda.CliHandler;
import sncr.bda.ConfigLoader;
import sncr.bda.base.MetadataBase;
import sncr.bda.base.MetadataStore;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Parameter;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.metastore.ProjectStore;
import sncr.bda.services.AuditLogService;
import sncr.bda.services.DLDataSetService;
import sncr.bda.services.TransformationService;

import sncr.xdf.adapters.writers.MoveDataDescriptor;
import sncr.xdf.context.Context;
import sncr.xdf.exceptions.XDFException;

/**
 * Created by asor0002 on 9/8/2017.
 */
public abstract class Component {

  private static final Logger logger = Logger.getLogger(Component.class);

  protected String error;
  protected Context ctx;
  protected DLDataSetService md;
  protected AuditLogService als;
  protected TransformationService transformationMd;

  protected String componentName = "unnamed";
  protected ArrayList<MoveDataDescriptor> resultDataDesc;
  protected Map<String, Map<String, Object>> inputDataSets = null;
  protected Map<String, Map<String, Object>> outputDataSets = null;

  protected Map<String, Map<String, Object>> inputs = null;
  protected Map<String, Map<String, Object>> outputs = null;


  protected WithDataSetService.DataSetServiceAux dsaux;
  private Map<String, JsonElement> mdOutputDataSetMap;
  private Map<String, JsonElement> mdInputDataSetMap;
  private String transformationId;

    public enum DATASET {
        input,
        output,
        rejected
    };


  public String getError() {
    return error;
  }

  /**
   * Runs the component returns the result.
   *
   * @return Returns integer result
   *         0 - Success else Failure
   *
   */
  public int run() {
    int ret = initializeDataSets();
    if (ret == 0) {
      ret = execute();
      logger.debug("execute Return code = " + ret);
      if (ret == 0) {
        ret = move();
        logger.debug("move Return code = " + ret);
        if (ret == 0) {
          ret = archive();
          logger.debug("archive Return code = " + ret);
          //          if (ret == 0) {
          //          } else {
          //          }
        } else {
          logger.error("Could not complete archive phase!");
        }
      } else {
        logger.error("Could not complete execution phase!");
      }
    } else {
      logger.error("Could not generate/retrieve metadata phase!");
    }
    ret = finalize(ret);
    return ret;
  }

  protected int initializeDataSets() {
    try {
      transformationId = transformationMd
          .readOrCreateTransformation(ctx, ctx.componentConfiguration);
      if (this instanceof WithDataSetService) {
        final boolean[] failed = {false};
        WithDataSetService mddl = (WithDataSetService) this;
        if (ctx.componentConfiguration.getInputs() != null
            && ctx.componentConfiguration.getInputs().size() > 0) {
          logger.info("Extracting meta data");
          dsaux = new WithDataSetService.DataSetServiceAux(ctx, md);
          inputDataSets = mddl.discoverInputDataSetsWithMetadata(dsaux);
          inputs = mddl.discoverDataParametersWithMetaData(dsaux);
          logger.debug("Input datasets = " + inputDataSets);
          logger.debug("Inputs = " + inputs);
          mdInputDataSetMap = md.loadExistingDataSets(ctx, inputDataSets);
          mdInputDataSetMap.forEach((id, ids) -> {
            try {
              md.getDSStore().addTransformationConsumer(id, transformationId);
            } catch (Exception e) {
              failed[0] = true;
              error = String.format("Could not add transformation"
                  + " consumer: %s to input dataset %s", transformationId, id);
              logger.error(error, e);
              return;
            }
          });
        }
        if (failed[0]) {
          return -1;
        }
        if (ctx.componentConfiguration.getOutputs() != null
            && ctx.componentConfiguration.getOutputs().size() > 0) {
          if (dsaux == null) {
            dsaux = new WithDataSetService.DataSetServiceAux(ctx, md);
          }
          outputDataSets = mddl.buildPathForOutputDataSets(dsaux);
          logger.debug("OutputDatasets = " + outputDataSets);
          outputs = mddl.buildPathForOutputs(dsaux);
          logger.debug("Outputs = " + outputs);
        }

        mdOutputDataSetMap = new HashMap<>();
        final int[] rc = {0};
        ctx.componentConfiguration.getOutputs().forEach(o -> {
          logger.debug("Add output object to data object repository: " + o.getDataSet());

          if (!mddl.discoverAndvalidateOutputDataSet(outputDataSets.get(o.getDataSet()))) {
            error = "Could not validate output dataset: " + o.getDataSet();
            logger.error(error);
            rc[0] = -1;
            return;
          }

          ctx.setStartTs();
          JsonElement ds = md.readOrCreateDataSet(ctx, outputDataSets.get(o.getDataSet()));
          if (ds == null) {
            error = "Could not create metadata for output dataset: " + o.getDataSet();
            logger.error(error);
            rc[0] = -1;
            return;
          }
          logger.debug("Create/read DS and add it to Output object DS list");
          JsonObject dsObj = ds.getAsJsonObject();
          String id = dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString()).getAsString();
          logger.debug(String.format(
              "Add to output DataSet map document with ID: %s\n %s", id, ds.toString()));

          mdOutputDataSetMap.put(dsObj.getAsJsonPrimitive(DataSetProperties.Id.toString())
              .getAsString(), ds);

          String step = "Could not create activity log entry for DataSet: " + o.getDataSet();
          try {
            JsonObject ale = als.generateDSAuditLogEntry(ctx, "INIT",
                inputDataSets, outputDataSets);
            String aleId = als.createAuditLog(ctx, ale);
            step = "Could not update metadata of DataSet: " + o.getDataSet();
            md.getDSStore().updateStatus(id, "INIT", ctx.startTs, null, aleId, ctx.batchID);
          } catch (Exception e) {
            error = step;
            logger.error(error, e);
            rc[0] = -1;
            return;
          }

        });
        if (rc[0] != 0) {
          return rc[0];
        }
      }
      return 0;
    } catch (Exception e) {
      error = "component initialization (input-discovery/output-preparation) exception: "
          + ExceptionUtils.getFullStackTrace(e);
      logger.error(error);
      return -1;
    }
  }


  /**
   * This method:
   * Initializes component from command line parameters,
   * it calls underlying collectCommandLineParameters methods converting parameters to Map.
   *
   * @param args - command line parameters.
   * @return - 0 - Success, -1 - fail
   */

  public final int collectCommandLineParameters(String[] args) {
    CliHandler cli = new CliHandler();
    try {
      HFileOperations.init();

      Map<String, Object> parameters = cli.parse(args);
      String cfgLocation = (String) parameters.get(CliHandler.OPTIONS.CONFIG.name());
      String configAsStr = ConfigLoader.loadConfiguration(cfgLocation);
      if (configAsStr == null || configAsStr.isEmpty()) {
        throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter,
            "configuration file name");
      }

      String appId = (String) parameters.get(CliHandler.OPTIONS.APP_ID.name());
      if (appId == null || appId.isEmpty()) {
        throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter,
            "Project/application name");
      }

      String batchId = (String) parameters.get(CliHandler.OPTIONS.BATCH_ID.name());
      if (batchId == null || batchId.isEmpty()) {
        throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter,
            "batch id/session id");
      }

      String xdfDataRootSys = System.getProperty(MetadataBase.XDF_DATA_ROOT);
      if (xdfDataRootSys == null || xdfDataRootSys.isEmpty()) {
        throw new XDFException(XDFException.ErrorCodes.IncorrectOrAbsentParameter,
            "XDF Data root");
      }

      return init(configAsStr, appId, batchId, xdfDataRootSys);
    } catch (ParseException e) {
      error = "Could not parse CMD line: " + e.getMessage();
      logger.error(ExceptionUtils.getStackTrace(e));
      return -1;
    } catch (XDFException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      return -1;
    } catch (Exception e) {
      error = "Exception at component initialization " + e.getMessage();
      logger.error(ExceptionUtils.getStackTrace(e));
      return -1;
    }
  }


  /**
   * Initialise component specific details.
   * @param config Component Configuration
   * @param appId Application ID
   * @param batchId Batch ID (Irrelavant as of now)
   * @param xdfDataRootSys XDF Root location
   * @return Result of init operation
   * @throws Exception Incase not able to read metastore.
   */
  // From API
  // This method should not interact with any storage (hdfs, maprfs, es etc)
  // Ideally should never be overwritten
  // and always executed from custom initialization functions

  public final int init(String config, String appId, String batchId, String xdfDataRootSys)
      throws Exception {

    logger.trace("Configuration dump: \n" + config);

    ComponentConfiguration cfg = null;
    try {
      cfg = validateConfig(config);
      logger.info("Component configuration = " + cfg);
    } catch (Exception e) {
      error = "Configuration is not valid, reason : " + e.getMessage();
      logger.error(e);
      return -1;
    }
    if (cfg == null) {
      error = "Internal error: validation procedure returns null";
      logger.error(error);
      return -1;
    }

    logger.debug("Getting project metadata");
    ProjectStore prjStore = new ProjectStore(xdfDataRootSys); // maprfs:///var/sip ideally it should be maprfs:///var/sip/services/metadata
    JsonElement prj = prjStore.readProjectData(appId);
    logger.debug("Project metadata for " + appId + " is " + prj);

    JsonObject prjJo = prj.getAsJsonObject();
    JsonElement plp;
    List<Parameter> oldList = cfg.getParameters();
    if (prjJo.has(ProjectStore.PLP)) {
      plp = prjJo.get(ProjectStore.PLP);
      JsonArray plpArray = plp.getAsJsonArray();
      List<Parameter> newList = new ArrayList<>();
      plpArray.forEach(plpen -> {
        JsonObject plpObject = plpen.getAsJsonObject();
        Parameter parameter = new Parameter(plpObject.get("name")
            .getAsString(), plpObject.get("value")
            .getAsString());
        newList.add(parameter);
      });
      for (Parameter pn : newList) {
        final boolean[] found = {false};
        oldList.forEach(po -> {
          if (pn.getName().equalsIgnoreCase(po.getName())) {
            found[0] = true;
            return;
          }
        });
        if (!found[0]) {
          oldList.add(pn);
        }
      }
    }

    try {
      ctx = new Context(componentName, batchId, appId, cfg);
      ctx.user = "N/A";
    } catch (Exception e) {
      logger.error("Could not create context: ", e);
      return -1;
    }
    ctx.fs = HFileOperations.getFileSystem();
    ctx.fc = HFileOperations.getFileContext();

    if (this instanceof WithSparkContext) {
      ((WithSparkContext) this).initSpark(ctx);
    }
    if (this instanceof WithMovableResult) {
      resultDataDesc = new ArrayList<>();
    }

    try {
      md = new DLDataSetService(xdfDataRootSys);
      transformationMd = new TransformationService(xdfDataRootSys);
      //            dsaux = new WithDataSetService.DataSetServiceAux(ctx, md);
      als = new AuditLogService(md.getRoot());
    } catch (Exception e) {
      error = "Initialization of metadata services failed";
      logger.error(error, e);
      return -1;
    }

    return 0;
  }

  //dev
  protected abstract int execute();

  protected int move() {
    int ret = 0;
    if (this instanceof WithMovableResult) {
      ret = ((WithMovableResult) this).doMove(ctx, resultDataDesc);
    }
    return ret;
  }



  protected abstract int archive();

  protected ComponentConfiguration validateConfig(String config) throws Exception {
    return Component.analyzeAndValidate(config);
  }

  public static ComponentConfiguration analyzeAndValidate(String cfg) throws Exception {
    ComponentConfiguration config = new Gson().fromJson(cfg, ComponentConfiguration.class);
    return config;
  }


    private int finalize(int ret) {
        String status = (ret == 0) ? "SUCCESS" : ((ret == 1) ? "PARTIAL" : "FAILED");
        int []rc = {0};
        rc[0] = 0;
        try {
            md.writeDLFSMeta(ctx);
            ctx.setFinishTS();
            JsonObject ale = als.generateDSAuditLogEntry(ctx, status, inputDataSets, outputDataSets);
            String aleId = als.createAuditLog(ctx, ale);
            mdOutputDataSetMap.forEach((id, ds) -> {
                try {
                    //TODO:: move it after merge to appropriate place
                    ctx.transformationID = transformationId;
                    ctx.ale_id = aleId;
                    ctx.status = status;

                    //TODO:: Keep it optional, schema might not be available
                    String dsname = id.substring(id.indexOf(MetadataStore.delimiter)
                        + MetadataStore.delimiter.length());
                    Map<String, Object> outDataset = outputDataSets.get(dsname);

                    // Set record count
                    long recordCount = (long) outDataset.getOrDefault(DataSetProperties.RecordCount.name(),
                        (long) 0);
                    logger.trace("Extracted record count " + recordCount);

                    //Extract schema
                    JsonElement schema = (JsonElement) outDataset.get(DataSetProperties.Schema.name());
                    if (schema != null) {
                        logger.trace("Extracted schema: " + schema.toString());
                        md.updateDS(id, ctx, ds, schema, recordCount);
                    }
                } catch (Exception e) {
                    error = "Could not update DS/ write AuditLog entry to DS, id = " + id;
                    logger.error(error);
                    logger.error("Native exception: ", e);
                    rc[0] = -1;
                    return;
                }
            });
            transformationMd.updateStatus(transformationId, status, ctx.startTs,
                ctx.finishedTs, aleId, ctx.batchID);
        } catch (Exception e) {
            error = "Exception at job finalization: " + ExceptionUtils.getFullStackTrace(e);
            logger.error(e);
            return -1;
        }
        return rc[0];
    }

  protected abstract String mkConfString();

  @Override
  public String toString() {
    String strCtx = "Execution context: " + ctx.toString();
    String specificConfParams = mkConfString();
    return strCtx + "\n" + specificConfParams;
  }

  /**
   * Start the component. Usually called by the UI component.
   *
   * @param self Current object
   * @param dataLakeRoot Datalake root location
   * @param config Component specific config parameters
   * @param app Application ID
   * @param batch Batch ID
   * @return Result of the operation
   */
  public static int startComponent(Component self, String dataLakeRoot,
                                   String config, String app, String batch) {
    logger.debug(String.format("Component [%s] has been started...", self.componentName));
    logger.info("Configuration: " + config);
    logger.error("Configuration: " + config);

    try {
      if (self.init(config, app, batch, dataLakeRoot + Path.SEPARATOR + "services") == 0) {
        return self.run();
      } else {
        logger.error("Could not initialize component");
        return -1;
      }
    } catch (Exception e) {
      logger.error("Exception at start component: ", e);
      return -1;
    }
  }
}
