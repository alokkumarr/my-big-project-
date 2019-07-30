package sncr.xdf.parser.parsers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.InternalContext;

public class NGJsonFileParser implements FileParser {

  private InternalContext iCtx;

  public NGJsonFileParser(InternalContext iCtx) {
    this.iCtx = iCtx;
  }

  @Override
  public Dataset<Row> parseInput(String inputLocation) {
    Dataset<Row> inputDataset = iCtx.sparkSession.read().json(inputLocation);

    return inputDataset;
  }
}
