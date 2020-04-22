package sncr.xdf.sql.ng;

import io.prestosql.sql.tree.Statement;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sncr.bda.ConfigLoader;
import sncr.bda.conf.ComponentConfiguration;
import sncr.bda.conf.Input;
import sncr.bda.core.file.HFileOperations;
import sncr.xdf.context.NGContext;
import sncr.xdf.context.XDFReturnCode;
import sncr.xdf.exceptions.XDFException;
import sncr.xdf.ngcomponent.WithContext;
import sncr.xdf.sql.SQLDescriptor;
import sncr.xdf.sql.StatementType;
import sncr.xdf.sql.TableDescriptor;
import sncr.xdf.sql.spark.TestSparkContext;

import java.io.IOException;
import java.util.*;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
@RunWith(JUnit4.class)
public class SQLScriptDescriptorTest {

  private String script;
  private String configFile;
  private NGContext ngContext;
  private SparkSession session;
  private TestSparkContext context;
  private ComponentConfiguration configuration;

  @Before
  public void setUp() {
    System.setProperty("hadoop.home.dir", "c://hadoop");
    configFile = "sqlconfig.json";
    context = new TestSparkContext();
    session = context.getSparkSession();

    String configFilePath = getFileFromResource(configFile);
    configuration = ConfigLoader.parseConfiguration(ConfigLoader.loadConfiguration("file:///" + configFilePath));
    script = configuration.getSql().getScript();
    script = loadResourceAsString(script);
    ngContext = new NGContext("xdf", configuration, "sql001", "sql", "sq001p");
  }

  @After
  public void cleanUp() {
    session.close();
  }

  @Test
  public void testPrestoSqlParser() {
    NGSQLScriptDescriptor scriptDescriptor = new NGSQLScriptDescriptor(ngContext, "file:///output", ngContext.inputDataSets, ngContext.outputDataSets);
    script = NGSQLScriptDescriptor.removeComments(script);
    scriptDescriptor.preProcessSQLScript(script);
    scriptDescriptor.parsePrestoSQLScript();

    List<Statement> statements = scriptDescriptor.getParsedStatementList();
    Assert.assertEquals(3, statements.size());
  }

  protected String getFileFromResource(String resource) {
    ClassLoader loader = this.getClass().getClassLoader();
    return loader.getResource(resource).getPath();
  }

  public String loadResourceAsString(String fileName) {
    Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(fileName));
    String contents = scanner.useDelimiter("\\A").next();
    scanner.close();
    return contents;
  }
}
