package com.synchronoss.saw.batch.sftp.integration;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.synchronoss.sip.utils.IntegrationUtils;
import java.io.File;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;

public class SftpUtils {

  /**
   * This method is used to get the path of the file generated.
   * @param localDirectory localDirectory location.
   * @param entry the entry from sftp location.
   * @return string it will return file path.
   */
  public static String getFilePath(File localDirectory, LsEntry entry) {
    File file = new File(
        localDirectory.getPath() + File.separator + FilenameUtils.getBaseName(entry.getFilename())
            + "." + IntegrationUtils.renameFileAppender() + "."
            + FilenameUtils.getExtension(entry.getFilename()));
    return file.getPath();
  }

  /**
   * This method is used to get the path of the target file. 
   * @param localDirectory localDirectory location.
   * @param entry the entry from sftp location.
   * @return string it will return file path.
   */
  public static File createTargetFile(File localDirectory, LsEntry entry) {
    return new File(
        localDirectory.getPath() + File.separator + FilenameUtils.getBaseName(entry.getFilename())
            + "." + IntegrationUtils.renameFileAppender() + "."
            + FilenameUtils.getExtension(entry.getFilename()));
  }

  public static Date getActualRecDate(LsEntry entry) {
    return new Date(((long) entry.getAttrs().getATime()) * 1000L);
  }

  /**
   * Checks and adds if '/' is missing in beginning. Returns default drop location if destination is
   * null.
   * 
   * @param destinationLoc destination path.
   * @return destination location
   */
  public static String constructDestinationPath(String destinationLoc,
      String defaultDestinationLocation) {
    String destinationPath = "";
    if (destinationLoc == null) {
      destinationPath = defaultDestinationLocation;
    } else {
      if (destinationLoc.startsWith(File.separator)) {
        destinationPath = destinationLoc;
      } else {
        destinationPath = File.separator + destinationLoc;
      }
    }
    return destinationPath;

  }

}
