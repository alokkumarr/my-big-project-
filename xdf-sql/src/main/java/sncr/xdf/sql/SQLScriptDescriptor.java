package sncr.xdf.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.Context;
import sncr.xdf.conf.Parameter;
import sncr.xdf.datasets.conf.DataSetProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by srya0001 on 5/11/2017.
 */
public class SQLScriptDescriptor {

    private final String transactionalLocation;
    private final Context ctx;
    private String script;

    public String getScript() { return script; }

    private Map<String, String> parameterValues;
    private final static String patterns[] = { "(\\$\\{\\w+\\})|(\\$\\w+)" };

    private static final Logger logger = Logger.getLogger(SQLScriptDescriptor.class);

    private Statements stmts;
    private List<SQLDescriptor> statementDescriptors = new ArrayList<>();
    private Map<String, TableDescriptor> scriptWideTableMap = new HashMap<>();

    private Map<String, Map<String, String>> inputDataObjects;
    private Map<String, Map<String, String>> outputDataObjects;

    public SQLDescriptor getSQLDescriptor(int inx){
        return statementDescriptors.get(inx);
    }

    public List<SQLDescriptor> getSQLDescriptors(){
        return statementDescriptors;
    }

    public SQLScriptDescriptor(Context ctx,
                               String tempDir,
                               Map<String, Map<String, String>>  inputDOs,
                               Map<String, Map<String, String>> outputDOs){
        this.ctx = ctx;
        this.transactionalLocation = tempDir;
        inputDataObjects = inputDOs;
        outputDataObjects = outputDOs;
        parameterValues = extractSQLParameters(ctx.componentConfiguration.getParameters());
    }


    /**
     *  The method extracts parameters for SQL script.
     *  A parameter is presented as: "name":"parameter_name", "value":"parameter_value"
     */
    //TODO:: Rework the method to read parameters from 2 places:
    // - Component configuration
    // - /root/project/system/ctx/<>.jparm files by Batch ID
    private Map<String,String> extractSQLParameters(List<Parameter> parameters) {
        Map<String, String> sqlParams = new HashMap<>();
        for (sncr.xdf.conf.Parameter param : parameters) {
            logger.debug("Process parameter: " + param.getName() + " value: " + param.getValue());
            if ((param.getValue() == null || param.getValue().isEmpty())) {
                logger.error("Cannot set parameter: " + param.getName() + " value is Empty or null, skip it");
            }
            else
            {
                if(param.getName().toLowerCase().startsWith("sql")){
                    logger.info("Set SQL parameter: " + param.getName().substring(4) + " value " + param.getValue());
                    sqlParams.put(param.getName().substring(4), param.getValue());
                }
            }
        }
        return sqlParams;
    }


    /**
     * Prepare SQL script to be parsed.
     * 1. Read Partition context files and add partition based filters (to be developed)
     * 2. Replace ${entries} with parameter values.
     * @param a_script
     */
    public void preProcessSQLScript(String a_script) {
        script = a_script;

        logger.debug("Step 3: Replace partition base filters with actual entries");
        resolveReservedParameters();

        logger.debug("Step 4: Replace parameter entries with actual values:");

        for (int i = 0; i < patterns.length; i++) {

            // Create processMap Pattern object
            Pattern r = Pattern.compile(patterns[i]);
            // Now create matcher object.
            Matcher m = r.matcher(a_script);

            int position = 0;
//Should be only one iteration
            while (m.find(position)  ) {

                if(parameterValues == null || parameterValues.isEmpty())
                    throw new XDFException(XDFException.ErrorCodes.SQLScriptPreProcFailed, "The file has variable entries, but actual parameters are missing.");

                logger.trace( String.format("Found the text \"%s\" starting at index %d and ending at index %d.%n",m.group(),m.start(),m.end()));
                String varExpression = m.group().trim();
                if (varExpression.startsWith("${") &&  varExpression.endsWith("}")) {

                    String key = varExpression.substring(2, varExpression.length()-1);
                    if (parameterValues.containsKey(key)){
                        logger.debug( String.format("Replace: %s with %s",varExpression,parameterValues.get(key)));
                        script = script.replace(varExpression, parameterValues.get(key));
                    }
                }else if (varExpression.startsWith("$")){
                    String key = varExpression.substring(1);
                    if (parameterValues.containsKey(key)){
                        logger.trace( String.format("Replace: %s with %s",varExpression,parameterValues.get(key)));
                        script = script.replace(varExpression, parameterValues.get(key));
                    }
                }
//              else
// Should not be else
                position = m.end();
            }
            m.reset();
        }
    }


    /**
     * The method does the job,
     * it extracts recognizable SQL statements and builds SQL Script descriptors
     * that will be info source for SQL Executor calls
     */
    public void parseSQLScript(){
        try{
            logger.debug("Step 5: Parse SQL Script");
            if (script == null ) {
                logger.error("Internal error: Script was not pre-processed!");
                return;
            }

            stmts = CCJSqlParserUtil.parseStatements(script);

            // We have array of statements - check the table names
            // Since same table names will be mentioned multiple times in multiple
            // statements we have to support "grand" list and maintain precedence of the flags

            SqlScriptParser p = new SqlScriptParser();
            int i = 0;
            for(Statement stmt : stmts.getStatements()) {
                i++;
                List<TableDescriptor> tables = p.getTableList(stmt, i);
                logger.trace("Statement #" + i + " ==> " +  stmt.toString() + " table list size: " + ((tables != null)?tables.size():"no tables"));
                TableDescriptor targetTable = null;
                for(TableDescriptor td : tables){
                    logger.trace("Try table: " + td.toString());
                    if (td.isTargetTable) targetTable = td;
                    TableDescriptor existingTd = scriptWideTableMap.get(td.tableName);
                    if(existingTd == null){
                        // Table not in the list - just put it there
                        td.asReference.add(i);
                        scriptWideTableMap.put(td.tableName, td);
                    } else {
                        if (existingTd.isTargetTable && td.isTargetTable && !existingTd.isInDropStatement && !td.isInDropStatement)
                            throw new XDFException(XDFException.ErrorCodes.InvalidDataSources, td.tableName);
                        existingTd.asReference.add(i);
                    }

                } // <-- for(tableDescriptor..)

                SQLDescriptor sqlDesc = new SQLDescriptor();
                logger.trace("Qualify statement as: " + p.stType.toString());
//TODO:: Assume that statement is processMap select statement, generate correct name: add processed data object
                switch (p.stType) {

                    case SELECT:
                        logger.error("SELECT statement is not supported anymore, please use CREATE [TEMPORARY] TABLE AS statement");
                        throw new XDFException(XDFException.ErrorCodes.SQLScriptNotParsable);

                    case CREATE:

                    if (targetTable == null) {
                        throw new XDFException(XDFException.ErrorCodes.IncorrectSQL, " Target table was not found in table register.");
                    }
                    else {
                        sqlDesc.statementType = StatementType.CREATE;
                        sqlDesc.isTemporaryTable = targetTable.isTempTable;
                        if (!sqlDesc.isTemporaryTable) sqlDesc.targetObjectName = targetTable.tableName;
                        String s = stmt.toString().toLowerCase();
                        int pos = -1;
                        pos = s.indexOf("with");
                        if(pos < 0)
                            pos = s.indexOf("select");
                        if (pos < 0)
                            throw new XDFException(XDFException.ErrorCodes.IncorrectSQL, "Could not find SELECT clause for statement: " + stmt.toString());
                        sqlDesc.SQL = stmt.toString().substring(pos);
                        sqlDesc.tableDescriptor = targetTable;
                    }
                    break;

                    case DROP_TABLE:
                        if (targetTable == null)
                            throw new XDFException(XDFException.ErrorCodes.IncorrectSQL, "Could not determine target table for drop statement");
                        sqlDesc.statementType = StatementType.DROP_TABLE;
                        sqlDesc.tableDescriptor = targetTable;
                        sqlDesc.targetTableName = targetTable.tableName;
                        sqlDesc.SQL = stmt.toString();

                    break;
                    default:
                        throw new XDFException(XDFException.ErrorCodes.UnsupportedSQLStatementType);
                }
                sqlDesc.index = i;
                sqlDesc.targetTableName = targetTable.tableName;
                sqlDesc.transactionalLocation = transactionalLocation;
                sqlDesc.targetTransactionalLocation = sqlDesc.transactionalLocation + Path.SEPARATOR + sqlDesc.targetTableName;
                sqlDesc.targetTableMode = targetTable.mode;
                sqlDesc.targetTableFormat = targetTable.format;

                //TODO:: Format and Mode to SQL descriptor ???

                statementDescriptors.add(sqlDesc);
                logger.trace("SQL Statement descriptor: \n" + sqlDesc + "\n");
            }
            logger.debug("Table list: \n" + scriptWideTableMap );
        } catch(JSQLParserException e){
            throw new XDFException(XDFException.ErrorCodes.SQLScriptNotParsable, e);
        }
        return;
    }

    private final static String comment_patterns[] = { "\\-{2,}+.*\\n", "\\-{2,}+.*\\r\\n", "\\-{2,}+.*$", "/\\*(?:.|\\n)*?\\*/", "/\\*(?:.|\\r\\n)*?\\*/" };

    /**
     * The static function looks for one line PL/SQL comments: --
     * And then for multi-line C-style comments.
     * Returns SQL without comments.
     * @param sql2 SQL
     * @return SQL without comments
     */
    static String removeComments(String sql2) {
        StringBuilder sb = new StringBuilder(sql2.trim());
        for (int j = 0; j < comment_patterns.length; j++) {
            logger.trace(String.format("Pattern: %s %n Processing SQL: %s %n", comment_patterns[j], sb));
            Pattern p = Pattern.compile(comment_patterns[j]);
            Matcher m = p.matcher(sb);
            int position = 0;
            while (m.find(0)  ) {
                logger.trace( String.format("%n Found comment entry \"%s\" starting at index %d and ending at index %d.%n",m.group(),m.start(),m.end()));
                sb.delete(m.start(),m.end());
                logger.trace(String.format("Position %d: result: %s %n", position, sb.toString()));
                position = m.start();
            }
            m.reset();
        }
        return sb.toString();
    }


    /**
     * The methods reads context file:
     * - by Batch ID
     * and generated by Partition Maker
     */
    //TODO:: To be completed
    private void resolveReservedParameters() {

        String filter;
        if (ctx.componentConfiguration.getParameters() != null &&
            !ctx.componentConfiguration.getParameters().isEmpty())
        {
//TODO:: Re-construct partition filter support
//            filter = processedDataObject.getDetails().getPartitionFilter();
        }
        else{
            filter = "( 1 = 1 )";
            logger.debug("No partition filter is defined");
        }
/*
        String filter_entry = "${PARTITION_FILTER_" + processedDataObject.getName() + "}";
        if (script.contains(filter_entry)) {
            logger.debug("Replace partition filter entry [ " + filter_entry + " ] with filter string: " + filter);
            filter = "(" + filter + ")";
            script = script.replace(filter_entry, filter);
        }
*/
    }


    /**
     * The method matches referential table names with provided data sources
     * as follow:
     * - all source tables must be found in inputDataObjects
     */
    public void resolveTableNames() {
        logger.debug("Step 6: Resolve input table names");
        for (String tn : scriptWideTableMap.keySet()) {
            TableDescriptor td = scriptWideTableMap.get(tn);
            if (td.isTargetTable ^ td.isInDropStatement) continue;

            logger.trace("Resolving in table: " + tn);
            if (inputDataObjects.containsKey(tn)) {
                Map<String, String> doProps = inputDataObjects.get(tn);
                td.setLocation( doProps.get(DataSetProperties.PhysicalLocation.name()) );
                td.format = doProps.get( DataSetProperties.Format.name() );
                td.mode = doProps.get(DataSetProperties.Mode.name());
                logger.debug(String.format("Resolved table [%s] at location: %s, storage format: %s", tn, td.getLocation(), td.format));
            } else {
                throw new XDFException(XDFException.ErrorCodes.ConfigError, "Could not resolveDataParameters source data object: " + tn);
            }
        }
    }

    /**
     * The method matches referential target table names with provided output data objects
     * as follow:
     * - all destination (target) tables must be found in outputDataObjects
     */
    public void resultIntegrityCheck() {
        logger.debug("Step 7: Resolve target (output) table names");
        for (String tn : scriptWideTableMap.keySet()) {
            TableDescriptor td = scriptWideTableMap.get(tn);
            if (td.isTargetTable == td.isInDropStatement) continue;

            logger.trace("Resolving out table: " + tn);
            if (outputDataObjects.containsKey(tn)) {

                Map<String, String> oDO = outputDataObjects.get(tn);
                td.setLocation(oDO.get(DataSetProperties.PhysicalLocation.name()));
                td.format = oDO.get(DataSetProperties.Format.name());
                td.mode = oDO.get(DataSetProperties.Mode.name());
                td.numberOfFiles = Integer.valueOf(oDO.get(DataSetProperties.NumberOfFiles.name()));

                logger.debug(String.format("Resolved target table [%s => %s, storage format: %s, operation mode: %s, number of files %d ] \n  to location: ",
                        tn, td.getLocation(), td.format, td.mode, td.numberOfFiles));
            } else {
                throw new XDFException(XDFException.ErrorCodes.ConfigError, "Could not resolveDataParameters target data object: " + tn);
            }
        }
    }

    public Map<String, TableDescriptor> getScriptWideTableMap(){  return scriptWideTableMap; }

    public Statements getParsedStatements() {
        return stmts;
    }

    public enum StatementType {

        DROP_TABLE("Drop table"),
        UNKNOWN("Unknown type"),
        SELECT("Plain select"),
        CREATE("Create Table As Select");

        StatementType(String s) {
            subtitle = s;
        }

        String subtitle;

    }


}
