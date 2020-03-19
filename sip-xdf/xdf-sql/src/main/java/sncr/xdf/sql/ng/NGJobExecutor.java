package sncr.xdf.sql.ng;

import io.prestosql.sql.tree.Statement;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.core.file.HFileOperations;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.sql.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import sncr.xdf.context.XDFReturnCode;

/**
 * Created by srya0001 on 5/8/2016.
 * The class executes processMap SQL script per given object
 * Each SQL statement is executed separately, for each statement the Job executor
 * creates an SQL Executor objects.
 * The Job executor keeps track of all executed SQLs and their results.
 */
public class NGJobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(NGJobExecutor.class);

    private WithContext parent;
    private String script;

    private Map<String, Dataset<Row>> availableDataframes;
    //private NGSQLScriptDescriptor scriptDescriptor;
    private Map<String, SQLDescriptor> result;
    private List<SQLDescriptor> report;
    {
        availableDataframes = new HashMap<>();
        report = new ArrayList<>();
        result = new HashMap();
    }

    private String getScriptFullPath() {
        String sqlScript = this.parent.getNgctx().componentConfiguration.getSql().getScriptLocation() + Path.SEPARATOR + this.parent.getNgctx().componentConfiguration.getSql().getScript();
        logger.debug(String.format("Get script %s in location: ", sqlScript));
        return sqlScript;
    }

    public NGJobExecutor(WithContext parent) throws XDFException
    {
        this.parent = parent;
        if (this.parent.getNgctx().componentConfiguration.getSql().getScriptLocation().equalsIgnoreCase("inline")) {
            logger.debug("Script is inline encoded");
            script = new String (Base64.getDecoder().decode(this.parent.getNgctx().componentConfiguration.getSql().getScript()));
            logger.trace("Inline Script :" + script);
        }
        else {
            String pathToSQLScript = getScriptFullPath();
            logger.debug("Path to script: " + pathToSQLScript);
            try {
                script = HFileOperations.readFile(pathToSQLScript);
            } catch (FileNotFoundException e) {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, e, "Part to SQL script is not correct: " + pathToSQLScript);
            }
        }
        logger.trace("Script to execute:\n" +  script);

    }


    public int start(String tempDir) throws XDFException {
        int rc = 0;
        try {

            logger.debug(String.format("Temp dir: %s %n", tempDir ));
            NGSQLScriptDescriptor scriptDescriptor = new NGSQLScriptDescriptor(parent.getNgctx(), tempDir, parent.getNgctx().inputDataSets, parent.getNgctx().outputDataSets);
            logger.debug("Step 0: Remove comments: " + script);
            script = NGSQLScriptDescriptor.removeComments(script);
            scriptDescriptor.preProcessSQLScript(script);
            scriptDescriptor.parseSQLScript();

            if (parent.getNgctx().inputDataSets.size() > 0) {
                scriptDescriptor.resolveTableNames();
            }
            scriptDescriptor.resultIntegrityCheck();

            if (HFileOperations.exists(tempDir)) {
                logger.warn("Temporary directory " + tempDir + " already Exists: remove it before next attempt to process the batch");
                HFileOperations.deleteEnt(tempDir);
            }
            HFileOperations.createDir(tempDir);
            logger.trace("NGJobExecutor:start :"+tempDir);

            HFileOperations.exists(tempDir);

            List<Statement> statements = scriptDescriptor.getParsedStatements();

            for (int i = 0; i < statements.size(); i++) {

                SQLDescriptor descriptor = scriptDescriptor.getSQLDescriptor(i);

                if (descriptor.statementType == StatementType.UNKNOWN) continue;
                if (descriptor.statementType  == StatementType.CREATE) {

                    try {
                        NGSQLExecutor executor = new NGSQLExecutor(parent,descriptor, availableDataframes);
                        rc = executor.run(scriptDescriptor);
                        descriptor.result =(rc != 0)?"failed":"success";
                        if (rc != 0){
                            logger.error("Could not execute SQL statement: " + i );
                            return -1;
                        }
                        report.add(descriptor);

                    }catch (Exception e) {
                        logger.error("************** Cannot execute SQL: " + descriptor.SQL + " reason: ", e);
                        descriptor.result = "failed";
                        HFileOperations.deleteEnt(tempDir);
                        if (e instanceof XDFException) {
                            throw ((XDFException)e);
                        }else {
                            throw new XDFException(XDFReturnCode.INTERNAL_ERROR, e);
                        }
                    }
                }

                //TODO:: Debug, test and comment the DROP Table functionality
                else if (descriptor.statementType  == StatementType.DROP_TABLE) {

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
                    logger.trace("destination directory :"+destDir);
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
                    if (HFileOperations.exists(sqlDescriptor.transactionalLocation+Path.SEPARATOR+sqlDescriptor.targetTableName))
                        HFileOperations.deleteEnt(sqlDescriptor.transactionalLocation+Path.SEPARATOR+sqlDescriptor.targetTableName);
                    continue;
                }
                else{
                    logger.debug("Add result table: " + sqlDescriptor.targetTableName );
                    result.put(sqlDescriptor.targetTableName, sqlDescriptor);
                    Map<String, Object> ods = parent.getNgctx().outputDataSets.get(sqlDescriptor.targetTableName);
                    ods.put(DataSetProperties.Schema.name(), sqlDescriptor.schema);
                }
            }
        } catch (Exception e) {
            logger.error("Workaround exception logging: ", e);
            if (e instanceof XDFException) {
                throw ((XDFException)e);
            }else if (e instanceof IOException) {
                throw new XDFException(XDFReturnCode.EMBEDDED_EXCEPTION, e, "File System/IO");
            }else {
                throw new XDFException(XDFReturnCode.EMBEDDED_EXCEPTION, e, "Internal exception");
            }
        }
        return rc;
    }

    public Map<String, SQLDescriptor> getResultDataSets() { return result; }
}
