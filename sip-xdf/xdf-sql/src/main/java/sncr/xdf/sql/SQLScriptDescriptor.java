package sncr.xdf.sql;

import com.google.common.collect.ImmutableSet;
import io.prestosql.sql.parser.ParsingOptions;
import io.prestosql.sql.parser.SqlParser;
import io.prestosql.sql.parser.StatementSplitter;
import io.prestosql.sql.tree.Statement;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import sncr.bda.conf.Input;
import sncr.bda.conf.Output;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.context.Context;
import sncr.bda.conf.Parameter;
import sncr.bda.datasets.conf.DataSetProperties;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sncr.xdf.context.XDFReturnCode;

import static sncr.xdf.sql.StatementType.SELECT;

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
    private List<Statement> statementList = new ArrayList<>();
    private List<SQLDescriptor> statementDescriptors = new ArrayList<>();
    private Map<String, TableDescriptor> scriptWideTableMap = new HashMap<>();

    private Map<String, Map<String, Object>> inputDataObjects;
    private Map<String, Map<String, Object>> outputDataObjects;

    public SQLDescriptor getSQLDescriptor(int inx){
        return statementDescriptors.get(inx);
    }

    public List<SQLDescriptor> getSQLDescriptors(){
        return statementDescriptors;
    }

    private static final String LATERAL_VIEW = "lateral view.*$";

    public SQLScriptDescriptor(Context ctx,
                               String tempDir,
                               Map<String, Map<String, Object>>  inputDOs,
                               Map<String, Map<String, Object>> outputDOs){
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
        for (sncr.bda.conf.Parameter param : parameters) {
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

        logger.debug("Step 3: Replace parameter entries with actual values:");

        for (int i = 0; i < patterns.length; i++) {

            // Create processMap Pattern object
            Pattern r = Pattern.compile(patterns[i]);
            // Now create matcher object.
            Matcher m = r.matcher(a_script);

            int position = 0;
//Should be only one iteration
            while (m.find(position)  ) {

                if(parameterValues == null || parameterValues.isEmpty())
                    throw new XDFException(XDFReturnCode.SQL_SCRIPT_PRE_PROC_FAILED, "The file has variable entries, but actual parameters are missing.");

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
            logger.debug("Step 4: Parse JSQL SQL Script");
            if (script == null ) {
                logger.error("Internal error: Script was not pre-processed!");
                return;
            }

            stmts = CCJSqlParserUtil.parseStatements(script);

            logger.debug("SQL Statements = " + stmts);

            // We have array of statements - check the table names
            // Since same table names will be mentioned multiple times in multiple
            // statements we have to support "grand" list and maintain precedence of the flags

            SqlScriptParser p = new SqlScriptParser();
            int i = 0;
            for(net.sf.jsqlparser.statement.Statement stmt : stmts.getStatements()) {
                i++;
                List<TableDescriptor> tables = p.getTableList(stmt, i);
                logger.trace("Statement #" + i + " ==> " +  stmt.toString() + " table list size: "
                    + ((tables != null) ? tables.size() + " " +  tables : "no tables"));
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
                            throw new XDFException(XDFReturnCode.INVALID_DATA_SOURCES, td.tableName);
                        existingTd.asReference.add(i);
                    }

                } // <-- for(tableDescriptor..)

                SQLDescriptor sqlDesc = new SQLDescriptor();
                logger.trace("Qualify statement as: " + p.stType.toString());
//TODO:: Assume that statement is processMap select statement, generate correct name: add processed data object
                switch (p.stType) {

                    case SELECT:
                        logger.error("SELECT statement is not supported anymore, please use CREATE [TEMPORARY] TABLE AS statement");
                        throw new XDFException(XDFReturnCode.SQL_SCRIPT_NOT_PARSABLE);

                    case CREATE:

                        if (targetTable == null) {
                            throw new XDFException(XDFReturnCode.INCORRECT_SQL, " Target table was not found in table register.");
                        }
                        else {
                            sqlDesc.statementType = StatementType.CREATE;
                            sqlDesc.isTemporaryTable = targetTable.isTempTable;
                            if (!sqlDesc.isTemporaryTable) sqlDesc.targetObjectName = targetTable.tableName;
                            String s = stmt.toString().toLowerCase();
                            int pos = -1;

                            /**
                             * Fix for SIP-7744.  To avoid
                             * splitting queries which has key word 'WITH'
                             * any where in middle. Using trim to be
                             * safe with leading empty spaces
                             */
                            if(s.trim().startsWith("with")) {
                                pos = s.indexOf("with");
                            }
                            // pos = s.indexOf("with");
                            if(pos < 0)
                                pos = s.indexOf("select");
                            if (pos < 0)
                                throw new XDFException(XDFReturnCode.INCORRECT_SQL, "Could not find SELECT clause for statement: " + stmt.toString());
                            sqlDesc.SQL = stmt.toString().substring(pos);
                            sqlDesc.tableDescriptor = targetTable;
                        }
                        break;

                    case DROP_TABLE:
                        if (targetTable == null)
                            throw new XDFException(XDFReturnCode.INCORRECT_SQL, "Could not determine target table for drop statement");
                        sqlDesc.statementType = StatementType.DROP_TABLE;
                        sqlDesc.tableDescriptor = targetTable;
                        sqlDesc.targetTableName = targetTable.tableName;
                        sqlDesc.SQL = stmt.toString();

                        break;
                    default:
                        throw new XDFException(XDFReturnCode.UNSUPPORTED_SQL_STATEMENT_TYPE);
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
            throw new XDFException(XDFReturnCode.SQL_SCRIPT_NOT_PARSABLE, e);
        }
        return;
    }


    /**
     * The method does the job,
     * it extracts recognizable SQL statements and builds SQL Script descriptors
     * that will be info source for SQL Executor calls
     */
    public void prestoParseSQLScript(){
        try{
            logger.debug("Step 4: Parse Presto SQL Script");
            if (script == null ) {
                logger.error("Internal error: Script was not pre-processed!");
                return;
            }

            StatementSplitter splitter = new StatementSplitter(script, Collections.singleton(";"));
            List<StatementSplitter.Statement> stmtsList = splitter.getCompleteStatements();
            logger.debug("SQL Statements = " + stmtsList);

            // We have array of statements - check the table names
            // Since same table names will be mentioned multiple times in multiple
            // statements we have to support "grand" list and maintain precedence of the flags

            SqlParser parser = new SqlParser();
            PrestoSQLParser p = new PrestoSQLParser();
            int i = 0;
            for(StatementSplitter.Statement stmt : stmtsList) {
                i++;

                // check for temporary tables
                boolean isTemp = false;
                String query;
                if (haveTempTable(stmt, "temp")){
                    isTemp = true;
                    query = getParsableQuery(stmt, "temp");
                } else if (haveTempTable(stmt, "TEMP")){
                    isTemp = true;
                    query = getParsableQuery(stmt, "TEMP");
                } else if (haveTempTable(stmt, "TEMPORARY")){
                    isTemp = true;
                    query = getParsableQuery(stmt, "TEMPORARY");
                } else if (haveTempTable(stmt, "temporary")){
                    isTemp = true;
                    query = getParsableQuery(stmt, "temporary");
                } else {
                    query = stmt.statement();
                }

                // create statement for parsing
                Statement statement = parser.createStatement(query.replaceAll(LATERAL_VIEW, ""), new ParsingOptions());

                statementList.add(statement);
                List<TableDescriptor> tables = p.getTableList(statement, i, isTemp);
                logger.trace("Statement #" + i + " ==> " +  stmt.toString() + " table list size: "
                    + ((tables != null) ? tables.size() + " " +  tables : "no tables"));
                TableDescriptor targetTable = null;
                updateTableName(tables);
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
                            throw new XDFException(XDFReturnCode.INVALID_DATA_SOURCES, td.tableName);
                        existingTd.asReference.add(i);
                    }

                }

                SQLDescriptor sqlDesc = new SQLDescriptor();
                logger.trace("Qualify statement as: " + p.stType.toString());

                switch (p.stType) {

                    case SELECT:
                        logger.error("SELECT statement is not supported anymore, please use CREATE [TEMPORARY] TABLE AS statement");
                        throw new XDFException(XDFReturnCode.SQL_SCRIPT_NOT_PARSABLE);

                    case CREATE:

                    if (targetTable == null) {
                        throw new XDFException(XDFReturnCode.INCORRECT_SQL, " Target table was not found in table register.");
                    }
                    else {
                        sqlDesc.statementType = StatementType.CREATE;
                        sqlDesc.isTemporaryTable = targetTable.isTempTable;
                        if (!sqlDesc.isTemporaryTable) sqlDesc.targetObjectName = targetTable.tableName;
                        String s = stmt.toString().toLowerCase();
                        int pos = -1;
                        
                        /**
                         * Fix for SIP-7744.  To avoid
                         * splitting queries which has key word 'WITH'
                         * any where in middle. Using trim to be 
                         * safe with leading empty spaces
                         */
                        if(s.trim().startsWith("with")) {
                        	pos = s.indexOf("with");
                        }

                        if(pos < 0)
                            pos = s.indexOf("select");
                        if (pos < 0)
                            throw new XDFException(XDFReturnCode.INCORRECT_SQL, "Could not find SELECT clause for statement: " + stmt.toString());
                        sqlDesc.SQL = stmt.toString().substring(pos).replaceAll(";", "");
                        sqlDesc.tableDescriptor = targetTable;
                    }
                    break;

                    case DROP_TABLE:
                        if (targetTable == null)
                            throw new XDFException(XDFReturnCode.INCORRECT_SQL, "Could not determine target table for drop statement");
                        sqlDesc.statementType = StatementType.DROP_TABLE;
                        sqlDesc.tableDescriptor = targetTable;
                        sqlDesc.targetTableName = targetTable.tableName;
                        sqlDesc.SQL = stmt.toString();

                    break;
                    default:
                        throw new XDFException(XDFReturnCode.UNSUPPORTED_SQL_STATEMENT_TYPE);
                }
                sqlDesc.index = i;
                sqlDesc.targetTableName = targetTable.tableName;
                sqlDesc.transactionalLocation = transactionalLocation;
                sqlDesc.targetTransactionalLocation = sqlDesc.transactionalLocation + Path.SEPARATOR + sqlDesc.targetTableName;
                sqlDesc.targetTableMode = targetTable.mode;
                sqlDesc.targetTableFormat = targetTable.format;

                statementDescriptors.add(sqlDesc);
                logger.trace("SQL Statement descriptor: \n" + sqlDesc + "\n");
            }
            logger.debug("Table list: \n" + scriptWideTableMap );
        } catch(Exception e){
            throw new XDFException(XDFReturnCode.SQL_SCRIPT_NOT_PARSABLE, e);
        }
        return;
    }

    /**
     * Build the sanitized query for presto parse
     *
     * @param stmt
     * @param temp
     * @return sanitized query
     */
    private String getParsableQuery(StatementSplitter.Statement stmt, String temp) {
        return stmt.statement().replaceFirst(temp, "");
    }

    /**
     * Update the table name match with the configured table name.
     *
     * @param tables
     */
    private void updateTableName(List<TableDescriptor> tables) {
        if (tables != null && !tables.isEmpty()){
            for (TableDescriptor td : tables){
                logger.trace("table name start :" + td.toString());
                outputDataObjects.keySet().forEach(key -> {
                    if (key != null && key.equalsIgnoreCase(td.tableName)){
                        td.tableName = key;
                    }
                });
            }
        }
    }

    /**
     * Check temporary table exist
     *
     * @param stmt
     * @param temp
     * @return
     */
    private boolean haveTempTable(StatementSplitter.Statement stmt, String temp) {
        boolean isTemp = false;
        String[] wordArr = stmt.toString().split("\\s");
        for (String word : wordArr) {
            if (temp.equals(word)) {
                isTemp = true;
                break;
            }
        }
        return isTemp;
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
     * The method matches referential table names with provided data sources
     * as follow:
     * - all source tables must be found in inputDataObjects
     */
    public void resolveTableNames() {
        logger.debug("Step 5: Resolve input table names");
        for (String tn : scriptWideTableMap.keySet()) {
            TableDescriptor td = scriptWideTableMap.get(tn);
            if (td.isTargetTable ^ td.isInDropStatement) continue;

            //TODO:: Access by DataSet name or by parameter [name] -- Inputs???
            logger.trace("Resolving in table: " + tn);
            if (inputDataObjects.containsKey(tn)) {
                Map<String, Object> doProps = inputDataObjects.get(tn);
                td.setLocation((String) doProps.get(DataSetProperties.PhysicalLocation.name()));
                td.format = (String) doProps.get( DataSetProperties.Format.name() );
                td.mode = (String) doProps.get(DataSetProperties.Mode.name());
                logger.debug(String.format("Resolved table [%s] at location: %s, storage format: %s", tn, td.getLocation(), td.format));
            } else {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Could not resolveDataParameters source data object: " + tn);
            }
        }
    }

    /**
     * The method matches referential target table names with provided output data objects
     * as follow:
     * - all destination (target) tables must be found in outputDataObjects
     */
    public void resultIntegrityCheck() {
        logger.debug("Step 6: Resolve target (output) table names");
        for (String tn : scriptWideTableMap.keySet()) {
            TableDescriptor td = scriptWideTableMap.get(tn);
            if (td.isTargetTable == td.isInDropStatement) continue;
            if (td.isTargetTable && td.isTempTable) {
                td.format = Input.Format.PARQUET.toString();
                td.mode = Output.Mode.REPLACE.toString();
                td.numberOfFiles = 1;

                continue;
            }

            logger.debug("TD = " + td + ". Is temp table " + td.isTempTable);

            logger.trace("Resolving out table: " + tn);

            //TODO:: Access by DataSet name or by parameter [name] -- Outputs???
            //if (outputs.containsKey(tn)) {
            if (outputDataObjects.containsKey(tn)) {

                Map<String, Object> oDO = outputDataObjects.get(tn);
                td.setLocation((String) oDO.get(DataSetProperties.PhysicalLocation.name()));
                td.format = (String) oDO.get(DataSetProperties.Format.name());
                td.mode = (String) oDO.get(DataSetProperties.Mode.name());
                td.keys = (List<String>) oDO.get(DataSetProperties.PartitionKeys.name());
                td.numberOfFiles = (Integer) oDO.get(DataSetProperties.NumberOfFiles.name());

                logger.debug(String.format("Resolved target table [%s => %s, storage format: %s, operation mode: %s, number of files %d ] \n  to location: ",
                        tn, td.getLocation(), td.format, td.mode, td.numberOfFiles));
            } else {
                throw new XDFException(XDFReturnCode.CONFIG_ERROR, "Could not resolveDataParameters target data object: " + tn);
            }
        }
    }

    public Map<String, TableDescriptor> getScriptWideTableMap(){  return scriptWideTableMap; }

    public List<Statement> getPrestoStatements() {
        return statementList;
    }

    public Statements getParsedStatements() {
        return stmts;
    }

}
