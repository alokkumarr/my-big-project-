package sncr.xdf.dataprofiler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.JsonPrimitive;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;

//import static sncr.xdf.dataprofiler.CsvInspectorRowProcessor.T_DATETIME;
//import static sncr.xdf.dataprofiler.CsvInspectorRowProcessor.T_DOUBLE;
//import static sncr.xdf.dataprofiler.CsvInspectorRowProcessor.T_LONG;
//import static sncr.xdf.dataprofiler.CsvInspectorRowProcessor.T_NULL;
//import static sncr.xdf.dataprofiler.CsvInspectorRowProcessor.T_STRING;

public class ParquetWriterRecordProcessor extends ObjectRowProcessor {

    String javaScript;
    String[] phrases;

    static String schema = "{\"fields\": [{\"name\": \"F00\", \"type\": \"string\"},{\"name\": \"F01\",\"type\": \"long\"},{\"name\": \"F02\",\"type\": \"double\"}]}";


    public static void main(String[] args){
        ParquetWriterRecordProcessor p = new ParquetWriterRecordProcessor(schema);
        System.exit(0);
    }

    public ParquetWriterRecordProcessor(String schema){

        JsonArray fields = new JsonParser().parse(schema).getAsJsonObject().get("fields").getAsJsonArray();
        phrases = new String[fields.size()];

        javaScript = "Object[] data = new Object[" + fields.size() + "];\n";
        for(int i = 0; i < fields.size(); i++){
            String fieldName = fields.get(i).getAsJsonObject().get("name").getAsString();
            String fieldFiedType = fields.get(i).getAsJsonObject().get("type").getAsString();
            String phrase = createPhrase( i, fieldFiedType);
            phrases[i] = phrase;
            javaScript += phrase;
        }

        javaScript += "return data;\n";

        try {
            IScriptEvaluator se = CompilerFactoryFactory.getDefaultCompilerFactory().newScriptEvaluator();

            Object[] a = new Object[1];
            String[]   parameterNames         = {"in", "dummy"};
            Class<?>[] parameterTypes         = {a.getClass(), Long.TYPE};

            se.setParameters(parameterNames, parameterTypes);
            se.setReturnType(a.getClass());
            se.cook(javaScript);

            Object[] in = {"Hello", "42", "42.45"};
            Object[] arguments = {in, 10L};

            Object r = null;
            Long l = System.currentTimeMillis();
            for(int i = 1; i < 100000000; i++) {
                r = se.evaluate(arguments);
            }
            System.out.println("Processed in " + (System.currentTimeMillis() - l) + " millis.");

            if(r instanceof Object[]){
                Object[] retval = (Object[])r;
                for(int i = 0; i < retval.length; i++){
                    System.out.println("array[" + i + "](" + retval[i].getClass().getSimpleName() + ") = " + retval[i]);
                }
            }
        }catch(Exception e){
            System.out.println(javaScript);
            System.out.println(e.getMessage());
        }
    }

    private String createPhrase(int colNum, String type){

        /*
        String retval = "data[" + colNum + "]=";
        switch(type){
           // case T_DOUBLE:
               retval+="java.lang.Double.parseDouble(in[" + colNum + "].toString());\n";
                break;
            case T_LONG:
                retval+="java.lang.Long.parseLong(in[" + colNum + "].toString());\n";
                break;
            case T_STRING:
            default:
                retval+="in[" + colNum + "].toString();\n";
                break;
        }
        return retval;*/
        return "";
    }

    public static String CreateAvroParquetSchema(String xdfSchema){
        JsonObject result = new JsonObject();
        result.addProperty("type", "record");
        result.addProperty("name", "XDFdataset");
        result.addProperty("namespace", "xdf");

        JsonArray fields = new JsonArray();
        JsonArray srcFields = new JsonParser().parse(xdfSchema).getAsJsonObject().getAsJsonArray("fields");

        for( int i = 0; i <  srcFields.size(); i++){
            JsonObject srcField = srcFields.get(i).getAsJsonObject();
            JsonObject newField = new JsonObject();
            JsonArray arTypes = new JsonArray();
            arTypes.add(new JsonPrimitive("null"));
            switch(srcField.get("type").getAsString()){
              //  case T_DOUBLE :
              //  case T_LONG :
              //  case T_STRING:
              //  case T_DATETIME :
              //  case T_NULL :
                default:
                    arTypes.add(new JsonPrimitive("string"));

            }
            newField.add("type", arTypes);
            newField.addProperty("name", srcField.get("name").getAsString());
            newField.addProperty("default", "null");
            fields.add(newField);

        }
        result.add("fields", fields);
        return result.toString();
    }

    @Override
    public void rowProcessed(Object[] row, ParsingContext context) {

    }

}
