package sncr.xdf.parser.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
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

import static org.junit.Assert.assertEquals;

/**
 * This unit class to test inconsistent column use cases
 *
 * @author alok.kumarr
 * @since 3.6.0
 */
@RunWith(JUnit4.class)
public class ParserWithInconsistentColTest extends BaseTest {

  private Parser parser;
  private String configFile;
  private SparkSession session;
  private TestSparkContext context;
  private StructType parsedSchema;
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

    parser = configuration.getParser();

    parsedSchema = createSchema(parser.getFields(), true, true);
    originalSchema = createSchema(parser.getFields(), false, false);
  }

  @After
  public void cleanUp() {
    session.close();
    context.getJavaSparkContext().close();
    context = null;
  }

  @Test
  public void testInconsistentColumn() {
    String dataFilePath = getFileFromResource("inconsistentcolumn/inconsistentcol.dat");
    JavaRDD<String> rawData = context.getJavaSparkContext().textFile("file:///" + dataFilePath);

    ConvertToRow ctr = new ConvertToRow(originalSchema, createTsFormatList(parser.getFields()), parser.getLineSeparator(),
        parser.getDelimiter().charAt(0), parser.getQuoteChar().charAt(0), parser.getQuoteChar().charAt(0),
        '\'', context.getSparkContext().longAccumulator("ParserRecCounter"),
        context.getSparkContext().longAccumulator("ParserErrorCounter"), parser.getAllowInconsistentColumn());
    JavaRDD<Row> data = rawData.map(ctr);
    data.count();

    Dataset<Row> dataFrame = session.createDataFrame(data.rdd(), originalSchema);
    dataFrame.show();
    dataFrame.printSchema();

    long record = dataFrame.count();
    Assert.assertEquals(5l, record);

    List<Row> rowList = dataFrame.select("Field").collectAsList();
    Assert.assertEquals("null", rowList.get(3).mkString());
    Assert.assertEquals("Test string5", rowList.get(4).mkString());
    Assert.assertNotNull(dataFrame.select("ID").collectAsList().get(2).get(0));
  }

  @Test
  public void testParserWithDefaultConfig() {
    String dataFilePath = getFileFromResource("inconsistentcolumn/inconsistentcol.dat");
    JavaRDD<String> rawData = context.getJavaSparkContext().textFile("file:///" + dataFilePath);

    ConvertToRow ctr = new ConvertToRow(originalSchema, createTsFormatList(parser.getFields()), parser.getLineSeparator(),
        parser.getDelimiter().charAt(0), parser.getQuoteChar().charAt(0), parser.getQuoteChar().charAt(0),
        '\'', context.getSparkContext().longAccumulator("ParserRecCounter"),
        context.getSparkContext().longAccumulator("ParserErrorCounter"), false);
    JavaRDD<Row> data = rawData.map(ctr);

    int rejectedColumn = parsedSchema.length() - 2;
    JavaRDD<Row> filterData = data.filter(row -> (int) (row.get(rejectedColumn)) == 0);

    long finalCount = filterData.count();
    long expectedResult = 2;
    assertEquals(expectedResult, finalCount);
  }
}
