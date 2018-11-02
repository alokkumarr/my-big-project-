package com.synchronoss.saw.sftp.integration;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.springframework.integration.file.filters.AbstractFileListFilter;


/**
 * Make sure that the file adapter is polling of written completed on the box (might be sftp). 
 * It checks for lastModified time of file. 
 */
public class SipFileFilterOnLastModifiedTime extends AbstractFileListFilter<LsEntry> {
 
  private Long timeDifference = 1000L;
 
  /**
    * Filter a file based on last modification time.
    */
  @Override
    public boolean accept(LsEntry file) {
 
    long lastModified = file.getAttrs().getMTime();
    long currentTime = System.currentTimeMillis();
    return (currentTime - lastModified) > timeDifference;
  }
 
  public void setTimeDifference(Long timeDifference) {
    this.timeDifference = timeDifference;
  }
 
}