package com.synchronoss.saw.sftp.integration;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.integration.file.filters.AbstractFileListFilter;

public class SipSftpFilter extends AbstractFileListFilter<LsEntry> {

  private String filePatterns;
  private Integer numberOfFiles;
  private long totalSize;

  public String getFilePatterns() {
    return filePatterns;
  }

  public void setFilePatterns(String filePatterns) {
    this.filePatterns = filePatterns;
  }

  public Integer getNumberOfFiles() {
    return numberOfFiles;
  }

  public void setNumberOfFiles(Integer numberOfFiles) {
    this.numberOfFiles = numberOfFiles;
  }

  public Long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(Long totalSize) {
    this.totalSize = totalSize;
  }

  @Override
  public boolean accept(LsEntry file) {
    boolean acceptFiles = false;
    if (!file.getAttrs().isDir()) {
      String fileName = file.getFilename();
      Pattern pattern = Pattern.compile(filePatterns.toUpperCase());
      Matcher matcher = pattern.matcher(fileName.toUpperCase());
      if (matcher.matches()) {
        acceptFiles = true;
      } else {
        String[] patterns = filePatterns.replace("|", "#").split("#");
        for (String pat : patterns) {
          if (pat.endsWith("*") || pat.endsWith("%")) {
            String str = pat.replace("*", "");
            str = str.replace("%", "");
            if (fileName.toUpperCase().startsWith(str.toUpperCase())) {
              acceptFiles = true;
              break;
            }
          } else if (pat.startsWith("*") || pat.startsWith("%")) {
            String str = pat.replace("*", "");
            str = str.replace("%", "");
            if (fileName.toUpperCase().endsWith(str.toUpperCase())) {
              acceptFiles = true;
              break;
            }
          }
        }
      }
    }
    return acceptFiles;
  }
}
