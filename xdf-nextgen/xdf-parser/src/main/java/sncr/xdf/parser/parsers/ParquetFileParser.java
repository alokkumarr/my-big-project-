package sncr.xdf.parser.parsers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.Context;

public class ParquetFileParser implements FileParser {

  private Context ctx;

  public ParquetFileParser(Context ctx) {
    this.ctx = ctx;
  }

  @Override
  public Dataset<Row> parseInput(String inputLocation) {
    Dataset<Row> inputDataset = ctx.sparkSession.read().parquet(inputLocation);

    return inputDataset;
  }
}
