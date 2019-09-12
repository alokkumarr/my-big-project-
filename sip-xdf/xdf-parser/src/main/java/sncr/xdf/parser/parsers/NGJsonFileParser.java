package sncr.xdf.parser.parsers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.InternalContext;

public class NGJsonFileParser implements FileParser {

  private InternalContext iCtx;
  private static final Logger logger = Logger.getLogger(NGJsonFileParser.class);

  public NGJsonFileParser(InternalContext iCtx) {
    this.iCtx = iCtx;
  }

  public Dataset<Row> parseInput(String inputLocation,boolean multiLine) {

    logger.debug("Is multiLine : " + multiLine + "\n");
    Dataset<Row> inputDataset = iCtx.sparkSession.read().option("multiline", multiLine).json(inputLocation);

    return inputDataset;
  }

    @Override
    public Dataset<Row> parseInput(String inputLocation){

        Dataset<Row> inputDataset =iCtx.sparkSession.read().json(inputLocation);
        return inputDataset;
    }

}
