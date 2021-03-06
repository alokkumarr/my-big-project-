package sncr.xdf.sql;

import io.prestosql.sql.tree.Statement;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.conf.Sql;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.Context;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import sncr.xdf.context.XDFReturnCode;

/**
 * Created by srya0001 on 5/8/2016.
 * The class executes processMap SQL script per given object
 * Each SQL statement is executed separately, for each statement the Job executor
 * creates an SQL Executor objects.
 * The Job executor keeps track of all executed SQLs and their results.
 */
public class JobExecutor {

    private static final Logger logger = Logger.getLogger(JobExecutor.class);

    private Context ctx;

    Map<String, Map<String, Object>> inputDOs;
    Map<String, Map<String, Object>> outputDOs;

    private Map<String, Dataset<Row>> availableDataframes = new HashMap<>();
    private String now;

    private String script;
    private SQLScriptDescriptor scriptDescriptor;
    private Map<String, SQLDescriptor> result;

    public JobExecutor(Context ctx,
                       Map<String, Map<String, Object>> inputDOLocations,
                       Map<String, Map<String, Object>> outputDOLocations) throws XDFException
    {
        this.ctx = ctx;
        report = new ArrayList<>();
        result = new HashMap();
        inputDOs = inputDOLocations;
        outputDOs = outputDOLocations;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        now = format.format(new Timestamp(new Date().getTime()));
    }


    public void analyze(String script)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        this.now = format.format(new Timestamp(new Date().getTime()));
        this.script = script;
    }

    public int start(String tempDir) throws XDFException {

        int rc = 0;
        try {

            logger.debug(String.format("Temp dir: %s %n", tempDir ));
            scriptDescriptor = new SQLScriptDescriptor(ctx, tempDir, inputDOs, outputDOs);
            logger.debug("Step 0: Remove comments: " + script);
            script = SQLScriptDescriptor.removeComments(script);
            scriptDescriptor.preProcessSQLScript(script);
            Sql configSql = this.ctx.componentConfiguration.getSql();
            boolean isDisablePrestoParser =  configSql != null ? configSql.isDisablePrestoParser() : false;
            if (isDisablePrestoParser) {
                scriptDescriptor.parseSQLScript();
            } else {
                scriptDescriptor.prestoParseSQLScript();
            }
            scriptDescriptor.resolveTableNames();
            scriptDescriptor.resultIntegrityCheck();

            if (HFileOperations.exists(tempDir)) {
                logger.warn("Temporary directory " + tempDir + " already Exists: remove it before next attempt to process the batch");
                HFileOperations.deleteEnt(tempDir);
            }
            HFileOperations.createDir(tempDir);

            int statementSize;
            List<Statement> statements = scriptDescriptor.getPrestoStatements();
            if (statements != null && statements.size() > 0){
                statementSize = statements.size();
            } else {
                statementSize = scriptDescriptor.getParsedStatements() != null && scriptDescriptor.getParsedStatements().getStatements().size() > 0
                    ? scriptDescriptor.getParsedStatements().getStatements().size() : 0;
            }

            for (int i = 0; i < statementSize; i++) {

                SQLDescriptor descriptor = scriptDescriptor.getSQLDescriptor(i);
                if (descriptor.statementType == StatementType.UNKNOWN) continue;
                if (descriptor.statementType  == StatementType.CREATE) {

                    try {
                        SQLExecutor executor = new SQLExecutor(ctx,descriptor, availableDataframes);
                        rc = executor.run(scriptDescriptor);
                        descriptor.result =(rc != 0) ? "failed" : "success";
                        if (rc != 0){
                            logger.error("Could not execute SQL statement: " + i );
                            return -1;
                        }
                        report.add(descriptor);

                    } catch (Exception e) {
                        logger.error("Cannot execute SQL: " + descriptor.SQL + " reason: ", e);
                        logger.error("Remove temporary directory and cancel batch processing.");
                        descriptor.result = "failed";
                        HFileOperations.deleteEnt(tempDir);
                        return -1;
                    }
                }

                //TODO:: Debug, test and comment the DROP Table functionality
                else if (descriptor.statementType  == StatementType.DROP_TABLE) {
                    //TODO:: XDF-1013 implementation
                    // 1. Drop existing tables (remove data files) -- use resolved existing location

                    String destDir;
                    if (descriptor.tableDescriptor != null &&
                        descriptor.tableDescriptor.getLocation() != null &&
                        !descriptor.tableDescriptor.getLocation().isEmpty() ) {
                        logger.debug("Try location from statement table descriptor: " + descriptor.tableDescriptor.getLocation());
                        destDir = descriptor.tableDescriptor.getLocation();

                    }else{
                        //try global table
                        Map<String, TableDescriptor> globalMap = scriptDescriptor.getScriptWideTableMap();
                        TableDescriptor tblDesc = globalMap.get(descriptor.targetTableName);
                        destDir = tblDesc.getLocation();
                    }
                    descriptor.location = destDir;

                    if (descriptor.location != null && !descriptor.location.isEmpty() ) {

                        if (HFileOperations.exists(descriptor.location)) {
                            HFileOperations.deleteEnt(descriptor.location);
                            logger.debug(String.format("Table %s was successfully removed from location: %s", descriptor.targetTableName, destDir));
                        }
                    }
                    else{
                        logger.error("DROP TABLE statement cannot be executed: source data location for table: " + descriptor.tableDescriptor.tableName + " is Empty or null");
                        descriptor.result = "failed";
                        HFileOperations.deleteEnt(tempDir);
                        return -1;
                    }
                }
                logger.debug("SQL statement was successfully processed: " + descriptor.tableDescriptor.toString());
            }

            for( SQLDescriptor sqlDescriptor: report) {
                //Remove temporary tables/objects
                if (sqlDescriptor.isTemporaryTable) {
                    //Don't add descriptor for temp table in the result
                    logger.debug("Do not process temporary table: " + sqlDescriptor.targetTableName);
                    if (HFileOperations.exists(sqlDescriptor.targetTransactionalLocation))
                        HFileOperations.deleteEnt(sqlDescriptor.targetTransactionalLocation);
                    continue;
                }
                else{
                    logger.debug("Add result table: " + sqlDescriptor.targetTableName );
                    result.put(sqlDescriptor.targetTableName, sqlDescriptor);
                    Map<String, Object> ods = outputDOs.get(sqlDescriptor.targetTableName);
                    ods.put(DataSetProperties.Schema.name(), sqlDescriptor.schema);
                }
            }

        } catch (IOException e) {
            throw new XDFException(XDFReturnCode.EMBEDDED_EXCEPTION, e, "File System/IO");

        } catch (Exception e) {
            logger.error("Workaround exception logging: ", e);
            throw new XDFException(XDFReturnCode.EMBEDDED_EXCEPTION, e, "Internal exception");
        }
        return rc;
    }

    private List<SQLDescriptor> report;

    public Map<String, SQLDescriptor> getResultDataSets() { return result; }


}
