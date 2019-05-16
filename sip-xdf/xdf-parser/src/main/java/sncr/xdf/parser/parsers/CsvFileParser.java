package sncr.xdf.parser.parsers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.parser.Parser;

public class CsvFileParser implements FileParser {
  private static final Logger logger = Logger.getLogger(CsvFileParser.class);

  @Override
  public Dataset<Row> parseInput(String inputLocation) {
    // Refer to sncr.xdf.parser.Parser.java for CSVParser implementation
    logger.warn("Not implemented yet");
    return null;
  }
}
