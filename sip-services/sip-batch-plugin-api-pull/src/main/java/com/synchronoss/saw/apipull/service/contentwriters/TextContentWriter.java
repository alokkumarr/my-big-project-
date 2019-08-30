package com.synchronoss.saw.apipull.service.contentwriters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextContentWriter implements ContentWriter {
  private String content;
  private String contentType;

  public TextContentWriter(String content, String contentType) {
    this.content = content;
    this.contentType = contentType;
  }

  @Override
  public boolean write(String outputLocation) throws IOException {
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
