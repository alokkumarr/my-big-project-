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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import sncr.bda.core.file.HFileOperations;

public class SAWDelimitedReader {

  private static final org.apache.log4j.Logger logger =
      org.apache.log4j.Logger.getLogger(SAWDelimitedReader.class);

  private CsvParserSettings settings;
  private CsvParser parser;
  private SAWDelimitedRowProcessor rowProcessor;
  private String path;
  private boolean localFileSystem =false;

  public static void main(String[] args) {
    try {
      SAWDelimitedReader parser = new SAWDelimitedReader("/Users/spau0004/Desktop/*.csv", 4, true);
      parser.parseSomeLines();
      String str = parser.toJson();
      BufferedWriter writer =new BufferedWriter(new FileWriter("/Users/spau0004/Desktop/result.json"));
      writer.write(str);
      writer.close();
    } catch (Exception e) {
      logger.error("Exception while reading the raw file", e);
    }
    System.out.println("Total memory : " + Runtime.getRuntime().totalMemory());
    System.out.println("Free memory  : " + Runtime.getRuntime().freeMemory());
    System.out.println("Max memory   : " + Runtime.getRuntime().maxMemory());
    System.exit(0);
  }

  public SAWDelimitedReader(String root, long sampleSize, boolean localFileSystem) throws Exception {
    
    this.localFileSystem = localFileSystem;
    String tmp = "";
    if (root != null) {
      if (!this.localFileSystem){
      tmp = root + Path.SEPARATOR;}
      else {
        tmp = root;
      }
    }
    this.path = getFilePath(tmp);
    this.settings = new CsvParserSettings();
    this.settings.setIgnoreLeadingWhitespaces(true);
    this.settings.setIgnoreTrailingWhitespaces(true);
    this.rowProcessor = new SAWDelimitedRowProcessor(sampleSize);
    this.settings.setProcessor(this.rowProcessor);
    parser = new CsvParser(this.settings);
  }

  private String getFilePath(String path) throws Exception {
    String filePath = null;
    if (!localFileSystem) {
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

  public void parseSomeLines() throws Exception {
    Integer numberOfRowsParsed = 0;
    Reader reader = getReader(path);
    parser.beginParsing(reader);
    while (parser.parseNext() != null) {
      numberOfRowsParsed++;
    }
  }

  private Reader getReader(String path) throws Exception {
    File file = new File(path);
    InputStream inputStream = new FileInputStream(file);
    return new InputStreamReader(inputStream, "UTF-8");
  }

  public String toJson() {
    Gson gson = new GsonBuilder().setPrettyPrinting().setVersion(1.0).create();
    JsonObject jo = rowProcessor.toJson();
    JsonArray samples = jo.get("samples").getAsJsonArray();
    JsonElement formatElement = gson.toJsonTree(samples);
    JsonObject object = new JsonObject();
    object.add("samples", formatElement);
    return gson.toJson(object);
  }
}
