package sncr.bda.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.Path;

public class BdaCoreUtils {

  /**
   * normalize the path to eliminate the PathManipulation vulnarabilities.
   *
   * @param path to be normalized
   * @return normalized path
   */
  public static Path normalizePath(Path path) {
    String pathTobeNormalized = path.toString();
    String normalizePath = normalizePath(pathTobeNormalized);
    return new Path(normalizePath);
  }

  /**
   * normalize the path to eliminate the PathManipulation vulnarabilities.
   *
   * @param path to be normalized
   * @return normalized path
   */
  public static String normalizePath(String path) {
    return FilenameUtils.normalize(path);
  }
}
