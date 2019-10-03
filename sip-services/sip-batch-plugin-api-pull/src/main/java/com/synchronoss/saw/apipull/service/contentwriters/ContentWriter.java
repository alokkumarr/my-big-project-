package com.synchronoss.saw.apipull.service.contentwriters;

import java.io.IOException;

public interface ContentWriter {
  boolean write(String outputLocation) throws IOException;
}
