package com.synchronoss.saw.apipull.service.contentwriters;

import java.io.File;
import java.io.IOException;

public class FileContentWriter implements ContentWriter {

  private File file;

  public FileContentWriter(File file) {
    this.file = file;
  }

  @Override
  public boolean write(String outputLocation) throws IOException {
    return false;
  }
}
