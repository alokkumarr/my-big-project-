package sncr.bda.core.file;

import com.synchronoss.sip.utils.SipCommonUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class NioFileProcessor implements FileProcessor{

  private static final Logger logger = Logger.getLogger(NioFileProcessor.class);

  @Override
  public boolean isDestinationExists(String destination) throws Exception {
    String normalizedDestinationPath = SipCommonUtils.normalizePath(destination);
    File destinationPath = new File(normalizedDestinationPath);
    return destinationPath.exists();
  }

  @Override
  public void createDestination(String destination, StringBuffer connectionLogs)
      throws IOException {
    String normalizedDestinationPath = SipCommonUtils.normalizePath(destination);
    Files.createDirectories(Paths.get(normalizedDestinationPath));
  }

  @Override
  public void transferFile(InputStream stream, File localFile, String defaultLoc, String user) throws Exception {
    java.nio.file.Files.copy(stream, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
   // this.closeStream(stream);
  }

 
  @Override
  public boolean isFileExistsWithPermissions(String location) throws Exception {
    String normalizedLocationPath = SipCommonUtils.normalizePath(location);
    File destinationPath = new File(normalizedLocationPath);
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
    File fileDelete =new File(filePath);
    boolean isFileDeleted = fileDelete.delete();
    boolean isFolderDeleted = fileDelete.getParentFile().delete();
    
   return  isFileDeleted && isFolderDeleted;
  }

  @Override
  public int getDataFileBasedOnPattern(String filePath) throws Exception {
    logger.trace("Getting the status for the file pattern starts here :" + filePath);
    String normalizedFilePath = SipCommonUtils.normalizePath(filePath);
    String extension = FilenameUtils.getExtension(normalizedFilePath);
    File parentFileName = new File(normalizedFilePath);
    int size =
        parentFileName.list((directory, localfileName) -> localfileName.endsWith(extension)).length;
    logger
        .trace("Getting the status for the file pattern ends here :" + normalizedFilePath + " size :" + size);
    return size;
  }

 

}
