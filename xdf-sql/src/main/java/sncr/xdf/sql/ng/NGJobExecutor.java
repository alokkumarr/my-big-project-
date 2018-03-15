package sncr.xdf.sql.ng;

import net.sf.jsqlparser.statement.Statement;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.AbstractComponent;
import sncr.xdf.sql.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by srya0001 on 5/8/2016.
 * The class executes processMap SQL script per given object
 * Each SQL statement is executed separately, for each statement the Job executor
 * creates an SQL Executor objects.
 * The Job executor keeps track of all executed SQLs and their results.
 */
public class NGJobExecutor {

    private static final Logger logger = Logger.getLogger(NGJobExecutor.class);
    private AbstractComponent parent;
    private Map<String, Dataset<Row>> availableDataframes = new HashMap<>();
    private String script;
    private NGSQLScriptDescriptor scriptDescriptor;
    private Map<String, SQLDescriptor> result;

    public NGJobExecutor(AbstractComponent parent, String script) throws XDFException
    {
        report = new ArrayList<>();
        this.parent = parent;
        result = new HashMap();
        this.script = script;
    }


    public Long start(String tempDir) throws XDFException {

        Long rc = 0L;
        try {

            logger.debug(String.format("Temp dir: %s %n", tempDir ));
            scriptDescriptor = new NGSQLScriptDescriptor(parent.getICtx(), tempDir, parent.getNgctx().inputDataSets, parent.getNgctx().outputDataSets);
            logger.debug("Step 0: Remove comments: " + script);
            script = NGSQLScriptDescriptor.removeComments(script);
            scriptDescriptor.preProcessSQLScript(script);
            scriptDescriptor.parseSQLScript();
            scriptDescriptor.resolveTableNames();
            scriptDescriptor.resultIntegrityCheck();

            if (HFileOperations.exists(tempDir)) {
                logger.warn("Temporary directory " + tempDir + " already Exists: remove it before next attempt to process the batch");
                HFileOperations.deleteEnt(tempDir);
            }
            HFileOperations.createDir(tempDir);

            List<Statement> statements = scriptDescriptor.getParsedStatements().getStatements();

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
                            return -1L;
                        }
                        report.add(descriptor);

                    } catch (Exception e) {
                        logger.error("Cannot execute SQL: " + descriptor.SQL + " reason: ", e);
                        logger.error("Remove temporary directory and cancel batch processing.");
                        descriptor.result = "failed";
                        HFileOperations.deleteEnt(tempDir);
                        return -1L;
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
                        return -1L;
                    }
                }
                logger.debug("SQL statement was successfully processed: " + descriptor.tableDescriptor.toString());
            }

            for( SQLDescriptor sqlDescriptor: report) {
                //Remove temporary tables/objects
                if (sqlDescriptor.isTemporaryTable) {
                    logger.debug("Do not process temporary table: " + sqlDescriptor.targetTableName);
                    if (HFileOperations.exists(sqlDescriptor.targetTransactionalLocation))
                        HFileOperations.deleteEnt(sqlDescriptor.targetTransactionalLocation);
                    continue;
                }
                else{
                    logger.trace("Add result table: " + sqlDescriptor.targetTableName );
                    result.put(sqlDescriptor.targetTableName, sqlDescriptor);
/*
                    Map<String, Object> ods = parent.getNgctx().outputDataSets.get(sqlDescriptor.targetTableName);
                    ods.put(DataSetProperties.Schema.name(), sqlDescriptor.schema);
*/
                }
            }

        } catch (IOException e) {
            throw new XDFException(XDFException.ErrorCodes.EmbeddedException, e, "File System/IO");

        } catch (Exception e) {
            logger.error("Workaround exception logging: ", e);
            throw new XDFException(XDFException.ErrorCodes.EmbeddedException, e, "Internal exception");
        }
        return rc;
    }

    private List<SQLDescriptor> report;

    public Map<String, SQLDescriptor> getResultDataSets() { return result; }


}
