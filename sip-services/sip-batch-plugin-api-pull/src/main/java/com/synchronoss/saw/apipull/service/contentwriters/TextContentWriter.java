package com.synchronoss.saw.apipull.service.contentwriters;

import com.synchronoss.saw.apipull.plugin.service.ApiPullServiceImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextContentWriter implements ContentWriter {
  private String content;
  private String contentType;
  private static final Logger logger = LoggerFactory.getLogger(TextContentWriter.class);

  public TextContentWriter(String content, String contentType) {
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public boolean write(String outputLocation) throws IOException {
    logger.debug("Inside TextContentWriter:write");
    if (outputLocation != null && outputLocation.length() != 0) {
      File file = new File(outputLocation);

      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write(content);
      fileWriter.flush();
      fileWriter.close();

      return true;
    } else {
      throw new IOException("Output location not specified");
    }
  }
}
