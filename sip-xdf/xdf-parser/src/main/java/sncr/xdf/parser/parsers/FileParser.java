package sncr.xdf.parser.parsers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface FileParser {
  Dataset<Row> parseInput(String inputLocation);
}
