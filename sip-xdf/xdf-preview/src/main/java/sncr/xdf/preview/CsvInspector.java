package sncr.xdf.preview;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import sncr.bda.core.file.HFileOperations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;

public class CsvInspector {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CsvInspector.class);

    private CsvParserSettings settings;
    private CsvParser parser;
    private Long parseTime;
    private CsvInspectorRowProcessor rowProcessor;
    private String requestedPath;
    private Path path;
    private Long rowsToInspect;
    private long headerSize;
    private long fieldNamesLine;

    public static void main(String[] args){

        try {
            String cnf =  new Scanner(new File(args[0])).useDelimiter("\\Z").next();
            CsvInspector parser = new CsvInspector(cnf, "");
            parser.parseSomeLines();
            //parser.printResults();
            String str = parser.toJson();
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\projects\\tools\\result.json"));
            writer.write(str);
            writer.close();
        } catch(Exception e){
            System.out.println("Exception :" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Total memory : " + Runtime.getRuntime().totalMemory());
        System.out.println("Free memory  : " + Runtime.getRuntime().freeMemory());
        System.out.println("Max memory   : " + Runtime.getRuntime().maxMemory());

        System.exit(0);
    }

    //c/projects/tools
    public CsvInspector(String jsonSettings, String root) throws Exception {
        JsonObject conf = new JsonParser().parse(jsonSettings).getAsJsonObject();
        JsonObject inspectorSettings = conf.getAsJsonObject("csvInspector");
        if(inspectorSettings != null) {
            String tmp = "";
            if(root != null){
                tmp = root + Path.SEPARATOR;
            }
            tmp += getSetting(inspectorSettings, "file", "");

            // Full path
            this.requestedPath = tmp;
            // Since full path may reference directory or file mask
            // We have to choose one file to inspect
            this.path = getFilePath(tmp);

            logger.info("\nInspecting " + this.path);


            String lineSeparator = getSetting(inspectorSettings, "lineSeparator", "\\n");
            char delimiter = getSetting(inspectorSettings, "delimiter", ",").charAt(0);
            char quoteChar = getSetting(inspectorSettings, "quoteChar", "\"").charAt(0);
            char quoteEscapeChar = getSetting(inspectorSettings, "quoteEscape", "\\").charAt(0);
            char charToEscapeQuoteEscaping = getSetting(inspectorSettings, "charToEscapeQuoteEscaping", "\\").charAt(0);
            this.headerSize = getSetting(inspectorSettings, "hederSize", 0L);
            this.fieldNamesLine = getSetting(inspectorSettings, "fieldNamesLine", 0L);
            long rowsToInspect = getSetting(inspectorSettings, "rowsToInspect", 10000L);
            long sampleSize = getSetting(inspectorSettings, "sampleSize", 100L);

            this.settings = new CsvParserSettings();

            this.settings.getFormat().setLineSeparator(lineSeparator);
            this.settings.getFormat().setDelimiter(delimiter);
            this.settings.getFormat().setQuote(quoteChar);
            this.settings.getFormat().setQuoteEscape(quoteEscapeChar);
            this.settings.getFormat().setCharToEscapeQuoteEscaping(charToEscapeQuoteEscaping);

            // skip leading whitespaces
            this.settings.setIgnoreLeadingWhitespaces(true);
            // skip trailing whitespaces
            this.settings.setIgnoreTrailingWhitespaces(true);

            JsonArray formats = conf.getAsJsonObject("csvInspector").get("dateFormats").getAsJsonArray();

            String[] dateFmt = new String[formats.size()];
            for(int i = 0; i < formats.size(); i++){
                dateFmt[i] = formats.get(i).getAsString();
            }

            this.rowProcessor = new CsvInspectorRowProcessor(this.headerSize, this.fieldNamesLine, dateFmt, sampleSize);
            this.settings.setProcessor(this.rowProcessor);
            parser = new CsvParser(this.settings);

            parseTime = 0L;
            this.rowsToInspect = rowsToInspect;

        } else {
            throw new Exception("Invalid format for csvInspector configuration JSON");
        }
    }

    private Path getFilePath(String path) {
        // path can point to directory, be file mask or point single file
        // we just need one file to process
        FileSystem fs = HFileOperations.getFileSystem();
        if (fs != null)
            try {
                FileStatus[] plist = fs.globStatus(new Path(path));
                for (FileStatus f : plist) {
                    if (f.isFile()) {
                        return f.getPath();
                    }
                }
            } catch (IOException e) {

            }
        return null;
    }

    private String getSetting(JsonObject settings, String name, String defValue){
        if(settings.get(name) != null)
            return settings.get(name).getAsString();
        else
            return defValue;
    }

    private Long getSetting(JsonObject settings, String name, Long defValue){
        if(settings.get(name) != null)
            return settings.get(name).getAsLong();
        else
            return defValue;
    }


    public void parseSomeLines() throws Exception {
        Long startTime = System.currentTimeMillis();
        Integer numberOfRowsParsed = 0;
        Reader reader = getReader(path.toString());

        parser.beginParsing(reader);
        while(parser.parseNext() != null && numberOfRowsParsed < (rowsToInspect - 1)) {
            numberOfRowsParsed++;
        }
        parseTime = System.currentTimeMillis() - startTime;
    }

    private Reader getReader(String path) throws Exception {

        InputStream inputStream = HFileOperations.readFileToInputStream(path);
        return new InputStreamReader(inputStream, "UTF-8");
    }

    public String toJson(){

        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setVersion(1.0)
            .create();

        JsonObject jo = rowProcessor.toJson();
        JsonObject parser = jo.get("parser").getAsJsonObject();

        parser.addProperty("files", requestedPath);
        parser.addProperty("lineSeparator", settings.getFormat().getLineSeparatorString());
        parser.addProperty("delimiter", settings.getFormat().getDelimiter());
        parser.addProperty("quoteChar", settings.getFormat().getQuote());
        parser.addProperty("quoteEscape", settings.getFormat().getQuoteEscape());
        parser.addProperty("headerSize", this.headerSize);
        parser.addProperty("fieldNameLine", this.fieldNamesLine);

        JsonObject info = jo.get("info").getAsJsonObject();
        info.addProperty("file", path.getName());
        info.addProperty("parseTime", parseTime);
        return gson.toJson(jo);
    }
}
