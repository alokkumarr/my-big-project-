package sncr.bda.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

public class NioFileProcessor implements FileProcessor{

  private static final Logger logger = Logger.getLogger(HFileOperations.class);
  
  @Override
  public boolean isDestinationExists(String destination) throws Exception {
    File destinationPath = new File(destination);
    return destinationPath.exists();
  }

  @Override
  public void createDestination(String destination, StringBuffer connectionLogs) throws IOException {
    Files.createDirectories(Paths.get(destination));
    
  }

  @Override
  public void transferFile(InputStream stream, File localFile, String defaultLoc, String user) throws Exception {
    java.nio.file.Files.copy(stream, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    this.closeStream(stream);
    
  }

 

  @Override
  public boolean isFileExistsWithPermissions(String location) throws Exception {
    File destinationPath = new File(location);
   return  destinationPath.canRead() 
          && destinationPath.canWrite() && destinationPath.canExecute();
  }

  @Override
  public void closeStream(InputStream stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (final IOException ignored) { 
      }
  }
    
  }

  @Override
  public String getFilePath(String defaultDataDropLocation, String destination, String batchId) {
    return defaultDataDropLocation   + File.separator 
        +  destination + File.separator + batchId + File.separator;
  }

  @Override
  public boolean deleteFile(String filePath, String defaultLoc, String user) throws IOException {
   return new File(filePath).delete();
    
  }

 

}