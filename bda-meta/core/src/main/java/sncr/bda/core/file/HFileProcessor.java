package sncr.bda.core.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.log4j.Logger;
import sncr.bda.utils.BdaCoreUtils;

public class HFileProcessor implements FileProcessor {

  private static final Logger logger = Logger.getLogger(HFileProcessor.class);

  @Override
  public boolean isDestinationExists(String destination) throws Exception {
    return HFileOperations.exists(destination);

  }

  @Override
  public void createDestination(String destination, StringBuffer connectionLogs) throws Exception {
    HFileOperations.createDir(destination);
  }

  @Override
  public boolean isFileExistsWithPermissions(String location) throws Exception {
    return isDestinationExists(location);
  }

  public void transferFile(InputStream stream, File localFile, String defaultLoc, String user)
      throws Exception {
    String location = defaultLoc.replace(maprFsPrefix, "");
    logger.trace("Default drop location::::" + defaultLoc);
    Configuration conf = new Configuration();
    conf.set("hadoop.job.ugi", user);
    FileSystem fs = FileSystem.get(URI.create(location), conf);
    String normalizedPath = BdaCoreUtils.normalizePath(localFile.getPath());
    FSDataOutputStream fos = fs.create(new Path(normalizedPath));
    IOUtils.copyBytes(stream, fos, 8192, false);
    fos.close();
    fs.close();

  }

  @Override
  public void closeStream(InputStream stream) {
    IOUtils.closeStream(stream);

  }

  @Override
  public String getFilePath(String defaultDataDropLocation, String destination, String batchId) {
    return defaultDataDropLocation.replace(FileProcessor.maprFsPrefix, "") + File.separator
        + destination + File.separator + batchId + File.separator;
  }

  @Override
  public boolean deleteFile(String filePath, String defaultLoc, String user) throws IOException {
    boolean isFileDeleted = false;
    String location = defaultLoc.replace(maprFsPrefix, "");
    logger.trace("Default drop location::::" + defaultLoc);
    Configuration conf = new Configuration();
    conf.set("hadoop.job.ugi", user);
    FileSystem fs = FileSystem.get(URI.create(location), conf);
    Path path = new Path(filePath);
    Path parent = path.getParent();
    if (fs.exists(path)) {
      isFileDeleted = fs.delete(path, true);
    }
    if( fs.exists(parent) && fs.
        listStatus(parent).length == 0) {
      fs.delete(parent,true);
    }
    return isFileDeleted;
  }

  @Override
  public int getDataFileBasedOnPattern(String filePath) throws Exception {
    return HFileOperations.getlistOfFileStatus(filePath) != null
        ? HFileOperations.getlistOfFileStatus(filePath).length
        : 0;
  }



}
