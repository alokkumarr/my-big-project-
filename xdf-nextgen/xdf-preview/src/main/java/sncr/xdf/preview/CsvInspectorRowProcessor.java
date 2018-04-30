package sncr.xdf.preview;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvInspectorRowProcessor extends ObjectRowProcessor {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CsvInspectorRowProcessor.class);
    
    public final static String T_STRING = "string";
    public final static String T_DOUBLE = "double";
    public final static String T_LONG = "long";
    public final static String T_DATETIME = "date";
    public final static String T_NULL = "unknown";

    private int SAMPLE_SIZE;
    private int DEV_SAMPLE_SIZE;

    private Integer rowCounter;
    private long headerSize = 0;
    private long fieldDefRowNumber;
    private Integer numberOfRowsParsed; //up to 2,147,483,647
    private Integer maxNumberOfFields;
    private Integer minNumberOfFields;

    private List<String> fieldNames;
    private Map<Integer, String> fieldTypes;
    private List<String> headerLines;
    private List<String> samples;
    private List<Object[]> parsedSamples;
    private int sampleCnt;
    private List<String> deviatedSamples;
    private List<Object[]> deviatedParsedSamples;
    private int devSampleCnt;


    String[] dateFmt;
    private DateFormat fmt;

    public CsvInspectorRowProcessor(long headerSize, long fieldDefRowNumber, String[] dateFmt, long sampleSize){
        this.rowCounter = 0;
        this.numberOfRowsParsed = 0;
        this.maxNumberOfFields = 0;
        this.minNumberOfFields = Integer.MAX_VALUE;
        this.headerSize = headerSize;
        this.fieldDefRowNumber = fieldDefRowNumber;
        this.fieldNames = new ArrayList<>();
        this.fieldTypes = new HashMap<>();
        this.headerLines = new ArrayList<>();

        // TODO: Need cleanup into to long
        this.SAMPLE_SIZE = (int)sampleSize;
        this.DEV_SAMPLE_SIZE = ((int)(sampleSize * 0.1) > 0) ? (int)(sampleSize * 0.1) : 3;

        this.samples = new ArrayList<>(SAMPLE_SIZE);
        this.deviatedSamples = new ArrayList<>(DEV_SAMPLE_SIZE);
        this.parsedSamples = new ArrayList<>(SAMPLE_SIZE);
        this.deviatedParsedSamples = new ArrayList<>(DEV_SAMPLE_SIZE);
        this.sampleCnt = 0;
        this.devSampleCnt = 0;
        if(dateFmt.length > 0) {
            this.dateFmt = dateFmt;
            this.fmt = new SimpleDateFormat(dateFmt[0]);
        } else{
            this.fmt = null;
            this.dateFmt = null;
        }
    }

    @Override
    public void rowProcessed(Object[] row, ParsingContext context) {
        //here is the row. Let's just print it.
        if(rowCounter < headerSize) {
            if(rowCounter == (fieldDefRowNumber - 1)) {
                processFieldNames(row);
            }
            String currentLine = context.currentParsedContent();
            headerLines.add(currentLine.substring(0, currentLine.indexOf(new String(context.lineSeparator()))));
        } else {
            if(row.length > maxNumberOfFields || row.length < minNumberOfFields) {
                if (row.length > maxNumberOfFields)
                    maxNumberOfFields = row.length;
                if (row.length < minNumberOfFields)
                    minNumberOfFields = row.length;

                if(numberOfRowsParsed > 0 && devSampleCnt < DEV_SAMPLE_SIZE){
                    // Collect sample of record which has size deviation
                    String currentLine = context.currentParsedContent();
                    deviatedSamples.add(currentLine.substring(0, currentLine.indexOf(new String(context.lineSeparator()))));
                    deviatedParsedSamples.add(row);
                    devSampleCnt++;
                }
            } else {
                if(sampleCnt < SAMPLE_SIZE){
                    // Collect regular sample
                    String currentLine = context.currentParsedContent();
                    samples.add(currentLine.substring(0, currentLine.indexOf(new String(context.lineSeparator()))));
                    parsedSamples.add(row);
                    sampleCnt++;
                }
            }
            processDataRow(row);
            numberOfRowsParsed++;
        }
        rowCounter++;
    }

    private void processFieldNames(Object[] names){
        for(Object o : names){
            fieldNames.add(o.toString());
        }
    }

    private void processDataRow(Object[] fields){
        int i =0;
        for(Object o : fields){
            if(o == null || o.toString().length() == 0){
                // Register NULL
                createOrAddType(i, T_NULL);
            } else {
                // Try to convert
                createOrAddType(i, getType(o, i));
            }
            i++;
        }

    }

    public void createOrAddType(int fieldNo, String type){
        if(fieldTypes.get(fieldNo) == null){
            fieldTypes.put(fieldNo, type);
        } else {
            if(!fieldTypes.get(fieldNo).equals(type)){
                fieldTypes.put(fieldNo, resolveType(fieldTypes.get(fieldNo), type));
            }
        }
    }

    private String getType(Object o, int columnNo){

        String s = o.toString();
        // Check if ALREADY EXISTING type for this column and may be adjust it if needed
        if(fieldTypes.size() > columnNo ){
            switch(fieldTypes.get(columnNo)){
                case T_STRING: {
                    // String is strongest type - once defined, it can not be changed
                    return T_STRING;
                }
                case T_DOUBLE : {
                    // Double is second strongest type - once defined,  it can be changed to String only
                    return confirmDouble(s);
                }
                case T_LONG : {
                    // Long column can mutate to Double or String
                    return confirmLong(s);
                }
                case T_DATETIME : {
                    return confirmDate(s, columnNo);
                }
                case T_NULL : {
                    // All previous observations were against null or empty values
                    return confirmLong(s);
                }
                //TODO: Implement date
                //
                //case "Date" : {
                //    // Long column can mutate to Double or String
                //    return confirmDate(s);
                //}
                default:
                    return T_STRING;
            } //<-- switch(...)
        } else {
            // This is a first time we checking this column
            // (either first data record or record with additional fields)

            // If first 2 characters are non digits - most likely it is a string (assumption)
            if (s.length() > 2
                && !Character.isDigit(s.charAt(0))
                && s.charAt(0) != '.'
                && s.charAt(0) != '+'
                && s.charAt(0) != '-')
                    return T_STRING;
            else
                // Try to confirm weakest type
                return confirmDate(s, columnNo);
        }
    }

    private String confirmDouble(String value){
        try {
            Double.parseDouble(value);
            return T_DOUBLE;
        } catch (final NumberFormatException e2) {
            return T_STRING;
        }
    }

    private String confirmLong(String value){
        try {
            Long.parseLong(value);
            return T_LONG;
        } catch (final NumberFormatException e2) {
            return confirmDouble(value);
        }
    }

    public String confirmDate(String value, int columnNo){
        if(fmt != null) {
            try {
                fmt.parse(value);
                return T_DATETIME;
            } catch (final ParseException e) {
                return confirmLong(value);
            }
        } else
            return confirmLong(value);
    }

    private String resolveType(String originalType, String newType){
        if(originalType.equals(T_STRING) || newType.equals(T_STRING))
            return T_STRING;
        else if(originalType.equals(T_DOUBLE) || newType.equals(T_DOUBLE))
            return T_DOUBLE;
        else if(originalType.equals(T_LONG) || newType.equals(T_LONG))
            return T_LONG;
        else
            return T_STRING;
    }

    public JsonObject toJson(){

        JsonObject result = new JsonObject();
        JsonObject parser = new JsonObject();
        JsonArray fields = new JsonArray();
        JsonObject parsedHeaderLine = new JsonObject();
        for(Map.Entry<Integer, String> e : fieldTypes.entrySet()){

            String fieldName = e.getKey() < fieldNames.size() ? fieldNames.get(e.getKey()) : "FIELD_" + e.getKey();
            String fieldType = e.getValue();
            if(fieldType.equals(T_NULL)){
                fieldType = T_STRING;
            }

            JsonObject fieldDef = new JsonObject();
            fieldDef.addProperty("name", fieldName);
            fieldDef.addProperty("type", fieldType);

            if(fieldType.equals(T_DATETIME)){
                fieldDef.addProperty("format", dateFmt[0]);
            }
            fields.add(fieldDef);

            parsedHeaderLine.addProperty(fieldName, fieldType);

        }
        parser.add("fields", fields);

        JsonObject info = new JsonObject();

        info.addProperty("totalLines", rowCounter);
        info.addProperty("dataRows", numberOfRowsParsed);
        info.addProperty("maxFields", maxNumberOfFields);
        info.addProperty("minFields", minNumberOfFields);

        JsonArray samplesRaw = new JsonArray();
        for(String s : headerLines){
            samplesRaw.add(new JsonPrimitive(s));
        }
        for(String s : deviatedSamples){
            samplesRaw.add(new JsonPrimitive(s));
        }
        for(String s : samples)
            samplesRaw.add(new JsonPrimitive(s));

        JsonArray samplesParsed = new JsonArray();

        samplesParsed.add(parsedHeaderLine);

        for(Object[] dl : deviatedParsedSamples){
            samplesParsed.add(createParsedLine(dl));
        }
        for(Object[] dl : parsedSamples){
            samplesParsed.add(createParsedLine(dl));
        }

        result.add("parser", parser);
        result.add("info", info);
        result.add("samplesRaw", samplesRaw);
        result.add("samplesParsed", samplesParsed);
        return result;
    }

    private JsonObject createParsedLine(Object[] parsedLine){
        JsonObject line = new JsonObject();
        int fieldNo = 0;
        for(Object o : parsedLine) {
            addParsedField(line, fieldNo, o);
            fieldNo++;
        }
        return line;
    }

    private void  addParsedField(JsonObject toLine, int fieldNo, Object value){
        String fieldName = fieldNo < fieldNames.size() ? fieldNames.get(fieldNo) : "FIELD_" + fieldNo;
        if(value != null && value.toString().length() > 0 ) {
            toLine.addProperty(fieldName, value.toString());
        } else {
            toLine.addProperty(fieldName, "");
        }
    }
}

