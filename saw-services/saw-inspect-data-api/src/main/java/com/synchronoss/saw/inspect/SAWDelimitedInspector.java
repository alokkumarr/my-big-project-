package com.synchronoss.saw.inspect;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import sncr.bda.core.file.HFileOperations;

public class SAWDelimitedInspector {
  private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SAWDelimitedInspector.class);
  private CsvParserSettings settings;
  private CsvParser parser;
  private SAWDelimitedInspectorRowProcessor rowProcessor;
  private String path;
  private Long rowsToInspect;
  private long headerSize;
  private long fieldNamesLine;
  private boolean localFileSystem =false;
  private String relativePath;

  public static void main(String[] args) {
    try {
      String cnf =
          "{\"inspect\":{\"lineSeparator\":\"\\n\",\"delimiter\":\"|\",\"quoteChar\":\"\",\"quoteEscapeChar\":\"\",\"headerSize\":\"1\",\"fieldNamesLine\":\"1\",\"dateFormats\":[],\"rowsToInspect\":100,\"delimiterType\":\"delimited\",\"header\":\"yes\"}}";
      SAWDelimitedInspector parser = new SAWDelimitedInspector(cnf, "/Users/spau0004/Desktop/neg.csv", true, "Desktop/neg.csv");
      parser.parseSomeLines();
      String str = parser.toJson();
      System.out.println(str);
      BufferedWriter writer =
          new BufferedWriter(new FileWriter("/Users/spau0004/Desktop/result.json"));
      writer.write(str);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("Exception during inspecting a file", e);
    }
    System.out.println("Total memory : " + Runtime.getRuntime().totalMemory());
    System.out.println("Free memory  : " + Runtime.getRuntime().freeMemory());
    System.out.println("Max memory   : " + Runtime.getRuntime().maxMemory());
    System.exit(0);
  }

  public SAWDelimitedInspector(String jsonSettings, String root, boolean localFileSystem, String relativePath) throws Exception {
    this.localFileSystem = localFileSystem;
    this.relativePath = relativePath;
    JsonObject conf = new JsonParser().parse(jsonSettings).getAsJsonObject();
    JsonObject inspectorSettings = conf.getAsJsonObject("inspect");
    if (inspectorSettings != null) {
      String tmp = "";
      if (root != null) {
        if (!this.localFileSystem){
          tmp = root + Path.SEPARATOR;}
          else {
            tmp = root;
          }
      }
      this.path = getFilePath(tmp);
      logger.info("\nInspecting " + this.path);
      String lineSeparator = getSetting(inspectorSettings, "lineSeparator", "\\n");
      char delimiter = getSetting(inspectorSettings, "delimiter", ",").charAt(0);
      char quoteChar = !getSetting(inspectorSettings, "quoteChar", "\"").isEmpty()? getSetting(inspectorSettings, "quoteChar", "\"").charAt(0): "'".charAt(0);
      char quoteEscapeChar = !getSetting(inspectorSettings, "quoteEscape", "\\").isEmpty()?getSetting(inspectorSettings, "quoteEscape", "\"").charAt(0): "\\\\".charAt(0);
      char charToEscapeQuoteEscaping = !getSetting(inspectorSettings, "charToEscapeQuoteEscaping", "\\\\").isEmpty()?getSetting(inspectorSettings, "charToEscapeQuoteEscaping", "\\").charAt(0):"\\".charAt(0);
      this.headerSize = getSetting(inspectorSettings, "headerSize", 0L);
      this.fieldNamesLine = getSetting(inspectorSettings, "fieldNamesLine", 0L);
      long rowsToInspect = getSetting(inspectorSettings, "rowsToInspect", 10000L);
      long sampleSize = getSetting(inspectorSettings, "sampleSize", rowsToInspect);
      this.settings = new CsvParserSettings();
      this.settings.getFormat().setLineSeparator(lineSeparator);
      this.settings.getFormat().setDelimiter(delimiter);
      this.settings.getFormat().setQuote(quoteChar);
      this.settings.getFormat().setQuoteEscape(quoteEscapeChar);
      this.settings.getFormat().setCharToEscapeQuoteEscaping(charToEscapeQuoteEscaping);
      this.settings.setIgnoreLeadingWhitespaces(true);
      this.settings.setIgnoreTrailingWhitespaces(true);
      this.settings.setEscapeUnquotedValues(true);
      JsonArray formats = conf.getAsJsonObject("inspect").get("dateFormats").getAsJsonArray();
      String[] dateFmt = new String[formats.size()];
      for (int i = 0; i < formats.size(); i++) {
        dateFmt[i] = formats.get(i).getAsString();
      }
      this.rowProcessor = new SAWDelimitedInspectorRowProcessor(this.headerSize,
          this.fieldNamesLine, dateFmt, sampleSize,settings);
      this.settings.setProcessor(this.rowProcessor);
      parser = new CsvParser(this.settings);
      this.rowsToInspect = rowsToInspect;
    } else {
      throw new Exception("Invalid format for csvInspector configuration JSON");
    }
  }

  private String getFilePath(String path) throws Exception {
    String filePath = null;
    if (!this.localFileSystem) {
      FileSystem fs = HFileOperations.getFileSystem();
      if (fs != null)
        try {
          FileStatus[] plist = fs.globStatus(new Path(path));
          for (FileStatus f : plist) {
            if (f.isFile()) {
              filePath = f.getPath().toString();
            }
          }
        } catch (IOException e) {
          logger.error("Exception occured while the reading the files from fileSystem", e);
        }
    } else {
      File file = new File(path);
      if (!file.isDirectory() && !file.isFile()) {
        String extension = FilenameUtils.getExtension(path);
        String basePath = FilenameUtils.getFullPathNoEndSeparator(path);
        File dir = new File(basePath);
        final FilenameFilter filter = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return name.endsWith(extension);
          }
        };
        File[] list = dir.listFiles(filter);
        if (list != null && list.length > 0) {
          filePath = list[0].getAbsolutePath();
        }
      } else {
        if (file.isDirectory()) {
          throw new FileNotFoundException(
              "Preview raw data cannot be done at directory. We need file for that");
        }
        if (file.isFile()) {
          filePath = file.getAbsolutePath();
        }
      }
    }
    return filePath;
  }

  private String getSetting(JsonObject settings, String name, String defValue) {
    if (settings.get(name) != null)
      return settings.get(name).getAsString();
    else
      return defValue;
  }

  private Long getSetting(JsonObject settings, String name, Long defValue) {
    if (settings.get(name) != null)
      return settings.get(name).getAsLong();
    else
      return defValue;
  }
  public void parseSomeLines() throws Exception {
    Integer numberOfRowsParsed = 0;
    Reader reader = getReader(path);

    parser.beginParsing(reader);
    while (parser.parseNext() != null && numberOfRowsParsed < (rowsToInspect - 1)) {
      numberOfRowsParsed++;
    }
  }

  private Reader getReader(String path) throws Exception {
    InputStream inputStream = null;
    if (!this.localFileSystem) {
      inputStream = HFileOperations.readFileToInputStream(path);
      return new InputStreamReader(inputStream, "UTF-8");
    } else {
      File file = new File(path);
      inputStream = new FileInputStream(file);
      return new InputStreamReader(inputStream, "UTF-8");
    }
  }

  public String toJson() {
    Gson gson = new GsonBuilder().setPrettyPrinting().setVersion(1.0).create();
    JsonObject jo = rowProcessor.toJson();
    JsonObject info = jo.get("info").getAsJsonObject();
    info.addProperty("file", relativePath);
    return gson.toJson(jo);
  }
  
}
