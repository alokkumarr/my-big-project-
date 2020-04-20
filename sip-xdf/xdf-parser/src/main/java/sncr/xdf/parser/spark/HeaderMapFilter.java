package sncr.xdf.parser.spark;

import org.apache.spark.api.java.function.Function;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.exceptions.XDFException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeaderMapFilter implements Function<String, Iterable<String>> {

  private Integer headerLength;
  private Integer fieldDefRowNumber;
  private String lineSeparator;
  public static String HEADER_ROW;

  public HeaderMapFilter(Integer headerLength, String lineSeparator, Integer fieldDefRowNumber) {
    this.headerLength = headerLength;
    this.lineSeparator = lineSeparator;
    this.fieldDefRowNumber = fieldDefRowNumber;
  }

  @Override
  public Iterable<String> call(String s) {
    List<String> list = Arrays.asList(s.split(lineSeparator));
    if (headerLength < 1) {
      throw new XDFException(XDFReturnCode.CONFIG_ERROR, "headerSize should not be less than 1.");
    } else if (fieldDefRowNumber != null && fieldDefRowNumber < 0) {
      throw new XDFException(XDFReturnCode.CONFIG_ERROR, "fieldDefRowNumber should not be less than 0.");
    } else if (fieldDefRowNumber != null && headerLength < fieldDefRowNumber) {
      throw new XDFException(XDFReturnCode.CONFIG_ERROR, "fieldDefRowNumber should not be greater than headerSize");
    } else if (HEADER_ROW == null && headerLength == 1 && list != null && !list.isEmpty()) {
      HEADER_ROW = list.get(headerLength - 1);
    }
    return list.stream().skip(headerLength).collect(Collectors.toList());
  }
}
