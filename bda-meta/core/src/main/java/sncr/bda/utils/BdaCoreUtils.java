package sncr.bda.utils;

import com.synchronoss.sip.utils.SipCommonUtils;
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
    String normalizePath = SipCommonUtils.normalizePath(pathTobeNormalized);
    return new Path(normalizePath);
  }
}
