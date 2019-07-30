package sncr.xdf.parser.parsers;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.InternalContext;

public class NGParquetFileParser implements FileParser {

  private InternalContext ctx;

  public NGParquetFileParser(InternalContext ctx) {
    this.ctx = ctx;
  }

  @Override
  public Dataset<Row> parseInput(String inputLocation) {
    Dataset<Row> inputDataset = ctx.sparkSession.read().parquet(inputLocation);

    return inputDataset;
  }
}
