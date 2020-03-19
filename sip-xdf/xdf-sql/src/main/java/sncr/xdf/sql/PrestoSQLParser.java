package sncr.xdf.sql;

import io.prestosql.sql.tree.AstVisitor;
import io.prestosql.sql.tree.CreateTableAsSelect;
import io.prestosql.sql.tree.DropTable;
import io.prestosql.sql.tree.Join;
import io.prestosql.sql.tree.QueryBody;
import io.prestosql.sql.tree.QuerySpecification;
import io.prestosql.sql.tree.Relation;
import io.prestosql.sql.tree.Select;
import io.prestosql.sql.tree.Statement;
import io.prestosql.sql.tree.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
public class PrestoSQLParser extends AstVisitor<Object, Integer> {

  private static final String NOT_SUPPORTED_YET = "XDF Spark SQL does not support this statement";
  private Object dummyObject = new Object();
  private List<TableDescriptor> tables;
  private int statementIndex = 0;
  /**
   * There are special names, that are not table names but are parsed as tables. These names are
   * collected here and are not included in the tables - names anymore.
   */
  private List<String> otherItemNames;
  public StatementType stType = StatementType.UNKNOWN;

  /**
   * Initializes table names collector.
   */
  protected void init() {
    otherItemNames = new ArrayList<>();
    tables = new ArrayList<>();
  }

  /**
   * Main entry for this Tool class. A list of found tables is returned.
   *
   * @param
   * @return
   */
  public List<TableDescriptor> getTableList(Statement statement, int inx) {
    init();
    statementIndex = inx;
    statement.accept(this, 0);
    return tables;
  }


  @Override
  protected Object visitCreateTableAsSelect(CreateTableAsSelect create, Integer context) {
    // Process CREATE TABLE statement
    String tn = create.getName().toString();
    boolean isTemp = false;
    if (create.toString() != null
        && (create.toString().contains("TEMP") ||
        create.toString().contains("temp") ||
        create.toString().contains("TEMPORARY") ||
        create.toString().contains("temporary"))) {
      isTemp = true;
    }
    tables.add(new TableDescriptor(tn, isTemp, statementIndex, true));
    stType = StatementType.CREATE;

    // Process sub-select
    QueryBody queryBody = create.getQuery().getQueryBody();
    queryBody.accept(this, 0);
    return dummyObject;
  }

  @Override
  protected Object visitQueryBody(QueryBody queryBody, Integer context) {
    QuerySpecification plainQuery = (QuerySpecification) queryBody;
    if (plainQuery.getSelect() != null) {
      plainQuery.getSelect().accept(this, 0);
    }

    Optional<Relation> itemFrom = plainQuery.getFrom();

    if (itemFrom != null) {
      itemFrom.get().accept(this, 0);
    }

    if (itemFrom.get() != null && itemFrom.get() instanceof Join) {
      ((Join) itemFrom.get()).getRight().accept(this, 0);
    }

    stType = (stType == StatementType.UNKNOWN) ? StatementType.SELECT : stType;
    return dummyObject;
  }

  @Override
  protected Object visitSelect(Select select, Integer context) {
    return null;
  }

  @Override
  protected Object visitTable(Table tableName, Integer context) {
    String tableWholeName = tableName.getName().toString();
    if (!otherItemNames.contains(tableWholeName.toLowerCase())
        && !tables.contains(tableWholeName)) {
      tables.add(new TableDescriptor(tableWholeName, false, statementIndex, false));
    }
    return dummyObject;
  }

  @Override
  protected Object visitDropTable(DropTable drop, Integer context) {
    stType = StatementType.DROP_TABLE;
    String tn = drop.getTableName().toString();
    tables.add(new TableDescriptor(tn, statementIndex, true));
    return dummyObject;
  }
}
