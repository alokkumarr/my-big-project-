package sncr.xdf.parser.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sncr.bda.ConfigLoader;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Parser;
import sncr.xdf.parser.TestSparkContext;

import java.util.List;

/**
 * This unit class test the inconsistent column use case
 *
 * @author alok.kumarr
 * @since 3.6.0
 */
@RunWith(JUnit4.class)
public class InconsistentColumnParserTest extends BaseTest{

  private ConvertToRow ctr;
  private String configFile;
  private SparkSession session;
  private TestSparkContext context;
  private StructType originalSchema;
  private ComponentConfiguration configuration;

  @Before
  public void setUp() {
    System.setProperty("hadoop.home.dir", "c://hadoop");
    configFile = "inconsistentcolumn/parserconfig.json";
    context = new TestSparkContext();
    session = context.getSparkSession();

    String configFilePath = getFileFromResource(configFile);
    configuration = ConfigLoader.parseConfiguration(ConfigLoader.loadConfiguration("file:///" + configFilePath));

    Parser parser = configuration.getParser();
    originalSchema = createSchema(parser.getFields(), false, false);

    List<String> tsFormats = createTsFormatList(parser.getFields());
    String lineSeparator = parser.getLineSeparator();
    char delimiter = parser.getDelimiter().charAt(0);
    char quoteChar = parser.getQuoteChar().charAt(0);
    char quoteEscapeChar = parser.getQuoteEscape().charAt(0);

    LongAccumulator errCounter = context.getSparkContext().longAccumulator("ParserErrorCounter");
    LongAccumulator recCounter = context.getSparkContext().longAccumulator("ParserRecCounter");

    ctr = new ConvertToRow(originalSchema, tsFormats, lineSeparator, delimiter,
        quoteChar, quoteEscapeChar, '\'', recCounter, errCounter);
  }

  @After
  public void cleanUp(){
    session.close();
    ctr = null;
  }

  @Test
  public void testInconsistentColumn() {
    String dataFilePath = getFileFromResource("inconsistentcolumn/testdata.dat");
    JavaRDD<String> rawData = context.getJavaSparkContext().textFile("file:///" + dataFilePath);
    JavaRDD<Row> data = rawData.map(ctr);

    Dataset<Row> dataFrame = session.createDataFrame(data.rdd(), originalSchema);
    dataFrame.show();
    dataFrame.printSchema();

    long record = dataFrame.count();
    Assert.assertEquals(5l, record);

    List<Row> rowList = dataFrame.select("Field").collectAsList();
    Assert.assertNotNull(rowList.get(2));
  }
}
