package sncr.xdf.parser.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;
import org.apache.spark.util.LongAccumulator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import scala.Tuple2;
import sncr.bda.ConfigLoader;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Field;
import sncr.bda.conf.Parser;
import sncr.xdf.component.Component;
import sncr.xdf.parser.TestSparkContext;
import sncr.xdf.preview.CsvInspectorRowProcessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by skbm0001 on 21/2/2018.
 */

@Ignore
public class ConvertToRowTest {
    TestSparkContext context;
    SparkSession session;

    String configFile;
    String dataFile;

    ConvertToRow ctr;

    StructType originalSchema;
    StructType parsedSchema;

    ComponentConfiguration configuration;
    @Before
    public void setUp() {
        configFile = "parserconfig.json";
        dataFile = "parserdata.dat";
        context = new TestSparkContext();
        session = context.getSparkSession();

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
            quoteChar, quoteEscapeChar, '\'', recCounter, errCounter);
    }

    @Test
    public void testRejection() {
        ClassLoader loader = this.getClass().getClassLoader();
        String dataFilePath = loader.getResource(dataFile).getPath();


        int rejectedColumn = parsedSchema.length() - 2;

        JavaRDD<String> rawData = context.getJavaSparkContext()
            .textFile("file:///" + dataFilePath);

        JavaRDD<Row> data = rawData.map(ctr);


        JavaRDD<Row> filterData = data.filter(row -> (int)(row.get(rejectedColumn)) == 0);

        long finalCount = filterData.count();

        long expectedResult = 6;

        assertEquals(expectedResult, finalCount);
    }

    @Test
    public void testCountChar() {
        try {
            Method countChar = testableMethod(ConvertToRow.class,"countChar", String.class, char.class);

            int count = (int)countChar.invoke(ctr, "hello", 'l');

            assertEquals(2, count);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Method testableMethod (Class className, String methodName, Class ...args) throws NoSuchMethodException,
        IllegalArgumentException, InvocationTargetException {
        Method method = null;

        method = className.getDeclaredMethod(methodName, args);
        method.setAccessible(true);

        return method;
    }


    private static StructType createSchema(List<Field> fields, boolean addRejectedFlag, boolean addReasonFlag){

        StructField[] structFields = new StructField[fields.size() + (addRejectedFlag ? 1 : 0) + (addReasonFlag ? 1 : 0)];
        int i = 0;
        for(Field field : fields){

            StructField structField = new StructField(field.getName(), convertXdfToSparkType(field.getType()), true, Metadata.empty());
            structFields[i] = structField;
            i++;
        }

        if(addRejectedFlag){
            structFields[i] = new StructField("__REJ_FLAG", DataTypes.IntegerType, true, Metadata.empty());
        }

        if (addReasonFlag) {
            structFields[i+1] = new StructField("__REJ_REASON", DataTypes.StringType, true, Metadata.empty());
        }
        return  new StructType(structFields);
    }

    private static DataType convertXdfToSparkType(String xdfType){
        switch(xdfType){
            case CsvInspectorRowProcessor.T_STRING:
                return DataTypes.StringType;
            case CsvInspectorRowProcessor.T_LONG:
                return DataTypes.LongType;
            case CsvInspectorRowProcessor.T_DOUBLE:
                return DataTypes.DoubleType;
            case CsvInspectorRowProcessor.T_INTEGER:
                return DataTypes.IntegerType;
            case CsvInspectorRowProcessor.T_DATETIME:
                return DataTypes.TimestampType;
            default:
                return DataTypes.StringType;

        }
    }

    private static List<String> createTsFormatList(List<Field> fields){
        List<String> retval = new ArrayList<>();
        for(Field field : fields){
            if (field.getType().equals(CsvInspectorRowProcessor.T_DATETIME) &&
                field.getFormat() != null && !field.getFormat().isEmpty()){
                retval.add(field.getFormat());
            } else {
                retval.add("");
            }
        }
        return retval;
    }

}
