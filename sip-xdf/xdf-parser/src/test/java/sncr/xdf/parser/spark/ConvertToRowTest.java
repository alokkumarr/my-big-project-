package sncr.xdf.parser.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.*;
import org.apache.spark.util.LongAccumulator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sncr.bda.ConfigLoader;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Parser;
import sncr.xdf.parser.TestSparkContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by skbm0001 on 21/2/2018.
 */
@RunWith(JUnit4.class)
public class ConvertToRowTest extends BaseTest{

  private String dataFile;
  private ConvertToRow ctr;
  private String configFile;
  private StructType parsedSchema;
  private TestSparkContext context;
  private StructType originalSchema;
  private ComponentConfiguration configuration;

  @Before
  public void setUp() {
    System.setProperty("hadoop.home.dir", "c://hadoop");
    configFile = "parserconfig.json";
    dataFile = "parserdata.dat";
    context = new TestSparkContext();
    ClassLoader loader = this.getClass().getClassLoader();
    String configFilePath = loader.getResource(configFile).getPath();
    configuration = ConfigLoader.parseConfiguration(ConfigLoader.loadConfiguration("file:///" + configFilePath));

    Parser parser = configuration.getParser();
    originalSchema = createSchema(parser.getFields(), false, false);
    parsedSchema = createSchema(parser.getFields(), true, true);

    List<String> tsFormats = createTsFormatList(parser.getFields());
    String lineSeparator = parser.getLineSeparator();
    char delimiter = parser.getDelimiter().charAt(0);
    char quoteChar = parser.getQuoteChar().charAt(0);
    char quoteEscapeChar = parser.getQuoteEscape().charAt(0);

    LongAccumulator errCounter = context.getSparkContext().longAccumulator("ParserErrorCounter");
    LongAccumulator recCounter = context.getSparkContext().longAccumulator("ParserRecCounter");

    ctr = new ConvertToRow(originalSchema, tsFormats, lineSeparator, delimiter,
        quoteChar, quoteEscapeChar, '\'', recCounter, errCounter, false);
  }

  @After
  public void cleanUp(){
    context.getJavaSparkContext().close();
    context = null;
  }

  @Test
  public void testRejection() {
    ClassLoader loader = this.getClass().getClassLoader();
    String dataFilePath = loader.getResource(dataFile).getPath();
    int rejectedColumn = parsedSchema.length() - 2;

    JavaRDD<String> rawData = context.getJavaSparkContext().textFile("file:///" + dataFilePath);
    JavaRDD<Row> data = rawData.map(ctr);

    JavaRDD<Row> filterData = data.filter(row -> (int) (row.get(rejectedColumn)) == 0);

    long finalCount = filterData.count();
    long expectedResult = 6;
    assertEquals(expectedResult, finalCount);
  }

  @Test
  public void testCountChar() {
    try {
      Method countChar = testableMethod(ConvertToRow.class, "countChar", String.class, char.class);
      int count = (int) countChar.invoke(ctr, "hello", 'l');

      assertEquals(2, count);
    } catch (NoSuchMethodException e) {
      LOGGER.error(e.getMessage());
    } catch (IllegalAccessException e) {
      LOGGER.error(e.getMessage());
    } catch (InvocationTargetException e) {
      LOGGER.error(e.getMessage());
    }
  }

  @Test
  public void testValidateQuoteBalance() {
    try {
      Method validateQuoteBalance = testableMethod(ConvertToRow.class, "validateQuoteBalance",
          String.class, char.class);

      assertEquals(true, (boolean) validateQuoteBalance.invoke(ctr, "he\"ll\"o", '"'));
      assertEquals(false, (boolean) validateQuoteBalance.invoke(ctr, "he\"llo", '"'));

      // Strings with binary and quoted binary characters are valid
      assertEquals(true, validateQuoteBalance.invoke(ctr, "0\u0011\u0016Binary", '"'));
      assertEquals(true, validateQuoteBalance.invoke(ctr, "\"0\u0011\u0016Binary\"", '"'));
    } catch (NoSuchMethodException e) {
      LOGGER.error(e.getMessage());
    } catch (InvocationTargetException e) {
      LOGGER.error(e.getMessage());
    } catch (IllegalAccessException e) {
      LOGGER.error(e.getMessage());
    }
  }
}
