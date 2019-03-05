package com.synchronoss.saw;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MetadataTestUtils {

  private static final Logger logger = LoggerFactory.getLogger(MetadataTestUtils.class);
  /**
   * Read Json data from classpath.
   *
   * @param classpath
   * @return
   * @throws IOException
   */
  public static String getJsonString(String classpath) {
    Resource resource = new ClassPathResource(classpath);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readTree(resource.getFile()).toString();
    } catch (IOException e) {
      logger.error("Exception occured while reading the file from class path." + e);
    }
    return null;
  }
}
