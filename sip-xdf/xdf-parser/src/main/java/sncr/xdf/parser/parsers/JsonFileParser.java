package sncr.xdf.parser.parsers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.Context;

public class JsonFileParser implements FileParser {
  private Context ctx;

  public JsonFileParser(Context ctx) {
    this.ctx = ctx;
  }

  @Override
  public Dataset<Row> parseInput(String inputLocation) {
    Dataset<Row> inputDataset = ctx.sparkSession.read().json(inputLocation);

    return inputDataset;
  }
}
