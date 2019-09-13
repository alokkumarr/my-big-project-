package sncr.xdf.parser.parsers;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import sncr.xdf.context.Context;

public class JsonFileParser implements FileParser {
  private Context ctx;
    private static final Logger logger = Logger.getLogger(JsonFileParser.class);

    public JsonFileParser(Context ctx) {
    this.ctx = ctx;
  }

    public Dataset<Row> parseInput(String inputLocation,boolean multiLine) {

        logger.debug("Is multiLine : " + multiLine + "\n");
        Dataset<Row> inputDataset = ctx.sparkSession.read().option("multiline", multiLine).json(inputLocation);

        return inputDataset;
    }

    @Override
    public Dataset<Row> parseInput(String inputLocation){

        Dataset<Row> inputDataset =ctx.sparkSession.read().json(inputLocation);
        return inputDataset;
    }

}
