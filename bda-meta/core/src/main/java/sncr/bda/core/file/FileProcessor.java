package sncr.bda.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileProcessor {
  public static final String maprFsPrefix = "maprfs://";
  void transferFile(InputStream stream, File localFile, String defaultLoc, String user) throws Exception;

   default boolean isDestinationMapR(String destinationPath) {
     return destinationPath.startsWith(maprFsPrefix);
   }

  boolean isDestinationExists(String destination) throws Exception;

  void createDestination(String destination, StringBuffer connectionLogs) throws Exception;

  boolean isFileExistsWithPermissions(String location) throws Exception;
  
  void closeStream(InputStream stream);
  
  String getFilePath(String defaultDataDropLocation, String destination, String batchId);
  
  public boolean deleteFile(String filePath, String defaultLoc, String user) throws IOException;
}