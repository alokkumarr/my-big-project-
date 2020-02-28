package sncr.xdf.parser.spark;

import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
public class HeaderMapFilter implements Function<String, Iterable<String>> {

  Integer headerSize;

  public HeaderMapFilter(Integer headerSize){
    this.headerSize = headerSize;
  }

  @Override
  public Iterable<String> call(String s) {
    return Arrays.asList(s.split("\n")).stream().skip(headerSize).collect(Collectors.toList());
  }
}
