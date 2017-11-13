package synchronoss.util;

import com.google.gson.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import sncr.xdf.core.file.HFileOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by asor0002 on 4/24/2017.
 */

public class DDLtoXDFConverter {


    public static String DDL2 =  "--  File created - Monday-February-22-2016\n" + "\n" + "\n" + "--  DDL for TABLE INTAKE.APP_TYPE\n" + "\n" + "\n" + "  CREATE TABLE INTAKE.APP_TYPE\n" + "   (    APP_TYPE_ID int not null,\n" + "        APP_TYPE_NAME VARCHAR(200)\n" + "   ) ;\n" + "\n" + "\n" + "--  DDL for TABLE INTAKE.APP_TEAMSPACE_TYPE\n" + "\n" + "\n" + "  CREATE TABLE INTAKE.APP_TEAMSPACE_TYPE\n" + "   (    APP_TEAMSPACE_TYPE_ID int not null,\n" + "        APP_TEAMSPACE_TYPE_NAME VARCHAR(1020)\n" + "   ) ;\n" + "\n" + "\n" + "--  DDL for TABLE INTAKE.BUSINESS_UNIT\n";

    public static void main(String[] args){


        Options options = new Options();
        options.addOption("i", "input", true, "Input DDL file name.");
        options.addOption("o", "output", true, "Output directory.");

        try {
            CommandLineParser parser = new BasicParser();
            CommandLine cl = parser.parse(options, args);

            if(!cl.hasOption("i") || !cl.hasOption("o")){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "DDL2XDF", options );
                System.exit(-1);
            }

            String inputFile = cl.getOptionValue("i");
            String outputFile = cl.getOptionValue("o");

            String DDL = HFileOperations.readFile(inputFile);
            System.out.println("Reading DDL definitions from " + inputFile);
            Statements stmt = CCJSqlParserUtil.parseStatements(DDL);
            System.out.println("Found "  + stmt.getStatements().size() + " statements (DDL and others).");

            for(Statement s : stmt.getStatements()){
                if(s!= null && s instanceof CreateTable) {
                    CreateTable cts = (CreateTable)s;
                    Table tbl  = cts.getTable();
                    System.out.println("Data Object Name: " + tbl.getName());
                    System.out.println("Full Name: " + tbl.getFullyQualifiedName());

                    JsonObject objectDefinition = new JsonObject();
                    objectDefinition.add("type", new JsonPrimitive("delimited"));
                    objectDefinition.add("header", new JsonPrimitive(false));
                    objectDefinition.add("appendColumns", new JsonPrimitive(true));

                    JsonObject lineDefinition = new JsonObject();

                    List<ColumnDefinition> cols = cts.getColumnDefinitions();
                    if(cols != null && cols.size() > 0) {
                        lineDefinition.add("sourceNumberOfColumns", new JsonPrimitive(cols.size()));
                        lineDefinition.add("sourceCellSeparator", new JsonPrimitive(","));
                        lineDefinition.add("quoteChar", new JsonPrimitive("x22"));
                        JsonArray cell = new JsonArray();

                        int dstIdx = 0;

                        // Add columns from DDL
                        int srcIdx = 0;
                        for (ColumnDefinition col : cols) {
                            String colName = col.getColumnName();
                            ColDataType cdt = col.getColDataType();
                            String typeName = cdt.getDataType();

                            //System.out.println(colName + " : " + typeName);
                            if (cdt.getArgumentsStringList() != null && cdt.getArgumentsStringList().size() > 0) {
                                List<String> colTypeArgs = cdt.getArgumentsStringList();
                                //System.out.println(colTypeArgs);
                            }

                            JsonObject cellDefinition= new JsonObject();
                            cellDefinition.add("name",  new JsonPrimitive(colName));
                            cellDefinition.add("destinationColumnIndex", new JsonPrimitive(dstIdx));

                            JsonObject format = new JsonObject();
                            format.add("type", new JsonPrimitive(convertType(typeName)));

                            JsonObject origin = new JsonObject();
                            origin.add("index", new JsonPrimitive(srcIdx));

                            cellDefinition.add("format", format);
                            cellDefinition.add("origin", origin);
                            cell.add(cellDefinition);

                            srcIdx++;
                            dstIdx++;
                        }

                        // Add system fields
                        cell.add(createSystemField("SRC_FILE_NAME", "FILE_NAME", dstIdx));
                        dstIdx++;
                        cell.add(createSystemField("PROC_DT", "PROCESS_DATE", dstIdx));
                        dstIdx++;
                        cell.add(createSystemField("BATCH_ID", "BATCH_ID", dstIdx));
                        dstIdx++;

                        lineDefinition.add("cell", cell);
                        objectDefinition.add("line", lineDefinition);
                    } //<-- if(cols != null)

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(objectDefinition);
                    System.out.println(outputFile + File.separator + tbl.getName() + ".jobj");
                    writeFile(outputFile + File.separator + tbl.getName() + ".jobj", json);
                } //<-- id statement instance of CreateStatement
            }
            System.out.println("Done");


        } catch(JSQLParserException e) {
            e.printStackTrace();
            System.err.println("EXCEPTION: " + e.getMessage());
            System.exit(-1);
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("EXCEPTION: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "DDL2XDF", options );
            System.exit(-1);
        }

        System.exit(0);
    }

    //{"name":"SRC_FILE_NAME","origin" : { "sourceType" : "FILE_NAME"},"destinationColumnIndex":0,"format" : {"type" : "string"}},
    public static JsonObject createSystemField(String name, String sourceType,  int destIdx){
        JsonObject cellDefinition= new JsonObject();
        cellDefinition.add("name",  new JsonPrimitive(name));
        cellDefinition.add("destinationColumnIndex", new JsonPrimitive(destIdx));
        JsonObject origin = new JsonObject();
        origin.add("sourceType", new JsonPrimitive(sourceType));
        cellDefinition.add("origin", origin);

        JsonObject format = new JsonObject();
        format.add("type", new JsonPrimitive("string"));
        cellDefinition.add("format", format);

        return cellDefinition;
    }

    public static String convertType(String srcType){
        if(srcType.toUpperCase().contains("CHAR")){
            return "string";
        } else if(srcType.toUpperCase().contains("INT")){
            return "integer";
        } else if (srcType.toUpperCase().equals("DOUBLE")
                || srcType.toUpperCase().equals("FLOAT")
                || srcType.toUpperCase().equals("REAL")
                || srcType.toUpperCase().equals("NUMERIC")
                || srcType.toUpperCase().equals("NUMBER")){
            return "decimal";
        } else {
            return "string";
        }
    }

    public static String writeFile(String fileName, String text) throws FileNotFoundException {
        String data = "";
        try {
            OutputStream stream;
            Path path = new Path(fileName);
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(path.toUri(), conf);

            FSDataOutputStream o = fs.create(path);
            o.write(text.getBytes());

            o.close();
            fs.close();

        } catch (Exception e) {
            throw new FileNotFoundException("File not found on the provided location :" + e);
        }

        return data;
    }

}
