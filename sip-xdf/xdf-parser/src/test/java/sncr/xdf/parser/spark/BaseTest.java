package sncr.xdf.parser.spark;

import org.apache.log4j.Logger;
import org.apache.spark.sql.types.*;
import sncr.bda.conf.Field;
import sncr.xdf.preview.CsvInspectorRowProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base test class have all the utility method to support NG parser Test cases.
 *
 * @author alok.kumarr
 * @since 3.6.0
 */
public class BaseTest {

  protected static final Logger LOGGER = Logger.getLogger(BaseTest.class);


  protected String getFileFromResource(String resource) {
    ClassLoader loader = this.getClass().getClassLoader();
    return loader.getResource(resource).getPath();
  }

  protected Method testableMethod(Class className, String methodName, Class... args) throws NoSuchMethodException,
      IllegalArgumentException {
    Method method = className.getDeclaredMethod(methodName, args);
    method.setAccessible(true);
    return method;
  }

  protected static StructType createSchema(List<Field> fields, boolean addRejectedFlag, boolean addReasonFlag) {
    StructField[] structFields = new StructField[fields.size() + (addRejectedFlag ? 1 : 0) + (addReasonFlag ? 1 : 0)];
    int i = 0;
    for (Field field : fields) {
      StructField structField = new StructField(field.getName(), convertXdfToSparkType(field.getType()), true, Metadata.empty());
      structFields[i] = structField;
      i++;
    }
    if (addRejectedFlag) {
      structFields[i] = new StructField("__REJ_FLAG", DataTypes.IntegerType, true, Metadata.empty());
    }
    if (addReasonFlag) {
      structFields[i + 1] = new StructField("__REJ_REASON", DataTypes.StringType, true, Metadata.empty());
    }
    return new StructType(structFields);
  }

  protected static DataType convertXdfToSparkType(String xdfType) {
    switch (xdfType) {
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

  protected static List<String> createTsFormatList(List<Field> fields) {
    List<String> retval = new ArrayList<>();
    for (Field field : fields) {
      if (field.getType().equals(CsvInspectorRowProcessor.T_DATETIME) &&
          field.getFormat() != null && !field.getFormat().isEmpty()) {
        retval.add(field.getFormat());
      } else {
        retval.add("");
      }
    }
    return retval;
  }
}
