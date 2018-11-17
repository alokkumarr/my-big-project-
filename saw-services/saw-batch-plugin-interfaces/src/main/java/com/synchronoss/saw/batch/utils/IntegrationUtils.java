package com.synchronoss.saw.batch.utils;

import com.google.common.base.Joiner;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;


public final class IntegrationUtils {


  private IntegrationUtils() {
    throw new AssertionError("Object cannot be created, it is a final class");

  }

  public static final String RENAME_DATE_FORMAT = "MM-dd-yyyy-hh-mm-ss";
  public static final String TAR_GZIP_FILE_EXTENSION = "gz";
  public static final String ZIP_FILE_EXTENSION = "zip";
  public static final String TAR_FILE_EXTENSION = "tar";
  public static final String RAR_FILE_EXTENSION = "rar";
  public static final String BZ2_FILE_EXTENSION = "bz2";
  public static final String Z_FILE_EXTENSION = "z";
  public static final String PATTERN_TO_ATTACHED_WHILE_EXTRACTION = ".*\\.";
  public static final String ERROR_EXTENSION = "error";
  public static final String VALIDATION_ERROR_EXTENSION = "validationFailed";
  public static final String KEY = "Saw12345Saw12345";
  public static final SecretKey secretKey = new SecretKeySpec(KEY.getBytes(), "AES");

  public static String getRenameDateFormat() {
    return RENAME_DATE_FORMAT;
  }
  
  /**
   * This method helps to transpose list of file patterns.
   */
  public static String transponseListOfFilePatterns(List<String> filenamePatterns) {
    List<String> patterns = new ArrayList<String>();
    if (filenamePatterns != null) {
      for (String filenamePattern : filenamePatterns) {
        patterns.add(filenamePattern.toUpperCase());
      }
    }
    String patternString = Joiner.on("|").join(patterns);
    if (patternString.contains("*")) {
      patternString = StringUtils.replace(patternString, "*.", ".*\\.");
    }
    return patternString;
  }

  /**
   * This method is used when renaming a file is necessary.
   */
  public static String renameFileAppender() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RENAME_DATE_FORMAT);
    return simpleDateFormat.format(new Date());
  }


  /**
   * This method is used to copy the files from source to destination directories<br>
   * compare to other approaches, it is the fastest way to acheive the same using Java NIO.
   * @param source String.
   * @param destination String.
   * @throws Exception Exception.
   */
  public static void copyFilesUsingChannels(File source, File destination) throws Exception {
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    FileInputStream sourceFileStream = new FileInputStream(source);
    FileOutputStream destinationFileStream = new FileOutputStream(destination);
    try {
      inputChannel = sourceFileStream.getChannel();
      outputChannel = destinationFileStream.getChannel();
      long size = inputChannel.size();
      inputChannel.transferTo(0, size, outputChannel);
    } finally {
      sourceFileStream.close();
      destinationFileStream.close();
      inputChannel.close();
      outputChannel.close();
    }
  }

  /**
   * Compress (tar.gz) the input files to the output file.
   */
  public static File makeTarFileAndCompressGzip(Collection<File> files, File output)
      throws IOException {
    FileOutputStream fos = null;
    TarArchiveOutputStream taos = null;
    try {
      fos = new FileOutputStream(output);
      taos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(fos)));
      taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
      taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
      for (File f : files) {
        addFilesToCompression(taos, f, ".");
      }
    } finally {
      fos.flush();
      taos.flush();
      taos.close();
      fos.close();
    }
    return output;
  }

  /**
   * Does the work of compression and going recursive for nested directories.
   */
  private static void addFilesToCompression(TarArchiveOutputStream taos, File file, String dir)
      throws IOException {
    taos.putArchiveEntry(new TarArchiveEntry(file, dir + File.separator + file.getName()));
    if (file.isFile()) {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
      IOUtils.copy(bis, taos);
      taos.flush();
      taos.closeArchiveEntry();
      bis.close();
    } else if (file.isDirectory()) {
      for (File childFile : file.listFiles()) {
        addFilesToCompression(taos, childFile, file.getName());
      }
    } else {
      taos.flush();
      taos.closeArchiveEntry();
      taos.close();
    }

  }
}
