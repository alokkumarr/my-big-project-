package sncr.xdf.parser.spark;

import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HeaderMapFilter implements Function<String, Iterable<String>> {

  private Integer headerSize;
  private String lineSeparator;

  public HeaderMapFilter(Integer headerSize, String lineSeparator) {
    this.headerSize = headerSize;
    this.lineSeparator = lineSeparator;
  }

  @Override
  public Iterable<String> call(String s) {
    return Arrays.asList(s.split(lineSeparator)).stream().skip(headerSize).collect(Collectors.toList());
  }
}
