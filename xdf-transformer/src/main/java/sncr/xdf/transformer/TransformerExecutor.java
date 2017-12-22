package sncr.xdf.transformer;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by srya0001 on 12/21/2017.
 */
public class TransformerExecutor {

    private static  Class thrownExceptions[] =
            {
                    NullPointerException.class,
                    IllegalArgumentException.class,
                    Exception.class
            };

//    private static final Logger logger = Logger.getLogger(TransformerExecutor.class);

    private static final String strScript =
            "System.out.println(\"In-Row descriptor\");\n" +
            "for( String f : inRowDescriptor.keySet()) System.out.println(\"Field: \" + f + \" Class: \" + ((Class)inRowDescriptor.get(f)).getName());\n" +
            "System.out.println(\"Out-Row descriptor\");\n" +
            "for( String f : outRowDescriptor.keySet()) System.out.println(\"Field: \" + f + \" Class: \" + ((Class)outRowDescriptor.get(f)).getName());\n" +
            "System.out.println(\"Values in script\"); \n" +
            "for( String k: ROW.keySet())\n" +
            " { Object v = ROW.get(k); \n" +
            " System.out.print(\"Field: \" +  k );\n" +
            " String value =\n" +
            " (v instanceof Integer ||\n" +
            " v instanceof Long ||\n" +
            " v instanceof String ||\n" +
            " v instanceof Double ||\n" +
            " v instanceof Boolean ||\n" +
            " v.getClass().getName().equals(\"int\") ||\n" +
            " v.getClass().getName().equals(\"long\")  ||\n" +
            " v.getClass().getName().equals(\"double\"))? String.valueOf(v):\"not supported\";\n" +
            " System.out.println(\" Value: \" +  value);\n" +
            " };\n" +
            " return ROW;";


    public static void main(String[] args){

        if(args.length < 6){
            System.out.println("Usage: <app> <script>" +
                    " <coma-separated in-param list> " +
                    " <comma-separated in-type list> " +
                    " <comma-separated out-param list> " +
                    " <comma-separated out-type list> " +
                    " <comma-separated in-param value list>");
            System.exit(0);
        }

        TransformerExecutor exec = new TransformerExecutor();
        try {
            String scriptName = args[0];
//            String strScript = HFileOperations.readFile(scriptName);

            String[] inParamName = args[1].split(",");
            String[] inParamType = args[2].split(",");

            if (inParamName.length != inParamType.length) {
                System.err.println("Input parameters list of names does not match to list of types. Exiting.");
                System.exit(1);
            }

            String[] outParamName = args[3].split(",");
            String[] outParamType = args[4].split(",");

            if (outParamName.length != outParamType.length) {
                System.err.println("Output parameters list of names does not match to list of types. Exiting.");
                System.exit(1);
            }

            String[] arguments = args[5].split(",");

            // Create "ScriptEvaluator" object.
            IScriptEvaluator se = CompilerFactoryFactory.getDefaultCompilerFactory().newScriptEvaluator();
            se.setReturnType(Map.class);
//            se.setDefaultImports(optionalDefaultImports);

            Map<String, Object> inRow = new HashMap<>();
            Map<String, Class> inRowDesc = new HashMap<>();
            Map<String, Class> outRowDesc = new HashMap<>();

            for (int i = 0; i< inParamType.length; i++){
                Class inParameterTypes = stringToType(inParamType[i]);
                if (!arguments[i].isEmpty())
                    inRow.put(inParamName[i], arguments[i]);
                inRowDesc.put(inParamName[i],inParameterTypes);
            }

            for (int i = 0; i< outParamType.length; i++){
                Class outParameterType = stringToType(outParamType[i]);
                outRowDesc.put(outParamName[i],outParameterType);
            }

            System.out.println("In-Row descriptor");
            inRowDesc.forEach((f,c) -> System.out.println("Field: " + f + " Class: " + c.getName()));
            System.out.println("Out-Row descriptor");
            outRowDesc.forEach((f,c) -> System.out.println("Field: " + f + " Class: " + c.getName()));

            String[] inPNames = {"inRowDescriptor", "ROW", "outRowDescriptor"};
            Class[]  inPTypes = {Class.forName("java.util.Map"), Class.forName("java.util.Map"), Class.forName("java.util.Map")};
            se.setParameters(inPNames, inPTypes);
            se.setThrownExceptions(thrownExceptions);
            se.cook(strScript);


            Object[] evalArgs = {inRowDesc, inRow, outRowDesc};
            Map result = (Map) se.evaluate(evalArgs);

            System.out.println("Print values: ");
            result.forEach( (k,v) ->
            {
                System.out.print("Field:  " +  k);
                String value =
                (v instanceof Integer ||
                 v instanceof Long ||
                 v instanceof String ||
                 v instanceof Double ||
                 v instanceof Boolean ||
                 v.getClass().getName().equals("int") ||
                v.getClass().getName().equals("long")  ||
                v.getClass().getName().equals("double"))? String.valueOf(v):"not supported";
                System.out.println(" Value: " +  value);
            });

        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static Class stringToType(String inParamType) throws Exception {
        if ("boolean".equals(inParamType)) return boolean.class;
        if ("char".equals(inParamType))    return char.class;
        if ("int".equals(inParamType))     return int.class;
        if ("long".equals(inParamType))    return long.class;
        if ("double".equals(inParamType))  return double.class;
        try {
            System.out.println("Object type: " +  inParamType);
            return Class.forName(inParamType);
        } catch (ClassNotFoundException ex) {
            String m = "Could not convert " + inParamType + " to valid Java Class: " + ex.getMessage();
//            logger.error("Could not convert " + inParamType + " to valid Java Class: ", ex);
            throw new Exception(m);
        }
    }


}
