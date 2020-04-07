package sncr.xdf.sql;

import io.prestosql.sql.tree.AliasedRelation;
import io.prestosql.sql.tree.AstVisitor;
import io.prestosql.sql.tree.CreateTableAsSelect;
import io.prestosql.sql.tree.CreateView;
import io.prestosql.sql.tree.Delete;
import io.prestosql.sql.tree.DropTable;
import io.prestosql.sql.tree.Except;
import io.prestosql.sql.tree.Execute;
import io.prestosql.sql.tree.Insert;
import io.prestosql.sql.tree.Intersect;
import io.prestosql.sql.tree.Join;
import io.prestosql.sql.tree.Query;
import io.prestosql.sql.tree.QueryBody;
import io.prestosql.sql.tree.QuerySpecification;
import io.prestosql.sql.tree.Relation;
import io.prestosql.sql.tree.Select;
import io.prestosql.sql.tree.Statement;
import io.prestosql.sql.tree.Table;
import io.prestosql.sql.tree.Union;
import io.prestosql.sql.tree.With;
import io.prestosql.sql.tree.WithQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author alok.kumarr
 * @since 3.6.0
 */
public class PrestoSQLParser extends AstVisitor<Object, Integer> {

  private static final String NOT_SUPPORTED_YET = "XDF Spark SQL does not support this statement";
  private static final Integer NODE_CONTEXT_INDEX = 0;
  private List<TableDescriptor> tables;
  private int statementIndex = 0;
  private boolean isTemp = false;

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
  public List<TableDescriptor> getTableList(Statement statement, int inx, boolean isTemp) {
    init();
    this.isTemp = isTemp;
    this.statementIndex = inx;
    statement.accept(this, NODE_CONTEXT_INDEX);
    return tables;
  }


  @Override
  protected Object visitCreateTableAsSelect(CreateTableAsSelect create, Integer context) {
    // Process CREATE TABLE statement
    String tn = create.getName().toString();
    tables.add(new TableDescriptor(tn, isTemp, statementIndex, true));
    stType = StatementType.CREATE;

    // Process sub-select
    Query query = create.getQuery();
    Optional<With> queryWith = query.getWith();
    if (queryWith.isPresent()) {
      queryWith.get().accept(this, NODE_CONTEXT_INDEX);
    }

    QueryBody queryBody = create.getQuery().getQueryBody();
    queryBody.accept(this, NODE_CONTEXT_INDEX);
    return queryBody;
  }

  @Override
  protected Object visitWith(With with, Integer context) {
    List<WithQuery> withQueryList = with.getQueries();
    if (withQueryList != null && !withQueryList.isEmpty()) {
      for (WithQuery withQuery : withQueryList) {
        otherItemNames.add(withQuery.getName().toString());
        withQuery.getQuery().getQueryBody().accept(this, NODE_CONTEXT_INDEX);
      }
    }
    return withQueryList;
  }

  @Override
  protected Object visitQueryBody(QueryBody queryBody, Integer context) {
    if (queryBody instanceof QuerySpecification) {
      QuerySpecification plainQuery = (QuerySpecification) queryBody;
      Select select = plainQuery.getSelect();
      if (select != null) {
        select.accept(this, NODE_CONTEXT_INDEX);
      }

      Optional<Relation> from = plainQuery.getFrom();
      if (from.isPresent()) {
        from.get().accept(this, 0);
      }
    }

    stType = (stType == StatementType.UNKNOWN) ? StatementType.SELECT : stType;
    return queryBody;
  }


  @Override
  protected Object visitExcept(Except except, Integer context) {
    List<Relation> relationList = except.getRelations();
    if (relationList != null && !relationList.isEmpty()) {
      for (Relation relation : relationList) {
        relation.accept(this, NODE_CONTEXT_INDEX);
      }
    }
    return except;
  }

  @Override
  protected Object visitUnion(Union union, Integer context) {
    List<Relation> relationList = union.getRelations();
    if (relationList != null && !relationList.isEmpty()) {
      for (Relation relation : relationList) {
        relation.accept(this, NODE_CONTEXT_INDEX);
      }
    }
    return union;
  }

  @Override
  protected Object visitIntersect(Intersect intersect, Integer context) {
    List<Relation> relationList = intersect.getRelations();
    if (relationList != null && !relationList.isEmpty()) {
      for (Relation relation : relationList) {
        relation.accept(this, NODE_CONTEXT_INDEX);
      }
    }
    return intersect;
  }

  @Override
  protected Object visitJoin(Join join, Integer context) {
    if (join.getRight() != null) {
      join.getRight().accept(this, NODE_CONTEXT_INDEX);
    }
    if (join.getLeft() != null) {
      join.getLeft().accept(this, NODE_CONTEXT_INDEX);
    }
    return join;
  }

  @Override
  protected Object visitRelation(Relation relation, Integer context) {
    relation.accept(this, NODE_CONTEXT_INDEX);
    return relation;
  }

  @Override
  protected Object visitAliasedRelation(AliasedRelation aliasedRelation, Integer context) {
    aliasedRelation.getRelation().accept(this, NODE_CONTEXT_INDEX);
    return aliasedRelation;
  }

  @Override
  protected Object visitSelect(Select select, Integer context) {
    return select;
  }

  @Override
  protected Object visitTable(Table tableName, Integer context) {
    String tableWholeName = tableName.getName().toString();
    if (!otherItemNames.contains(tableWholeName.toLowerCase())
        && !tables.contains(tableWholeName)) {
      tables.add(new TableDescriptor(tableWholeName, false, statementIndex, false));
    }
    return tableName;
  }

  @Override
  protected Object visitDropTable(DropTable drop, Integer context) {
    stType = StatementType.DROP_TABLE;
    String tn = drop.getTableName().toString();
    tables.add(new TableDescriptor(tn, statementIndex, true));
    return drop;
  }

  @Override
  protected Object visitDelete(Delete node, Integer context) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET + " : Delete");
  }

  @Override
  protected Object visitInsert(Insert node, Integer context) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET + " : Insert");
  }

  @Override
  protected Object visitStatement(Statement node, Integer context) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET + " : ALTER");
  }

  @Override
  protected Object visitCreateView(CreateView node, Integer context) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET + " : CREATE VIEW");
  }

  @Override
  protected Object visitExecute(Execute node, Integer context) {
    throw new UnsupportedOperationException(NOT_SUPPORTED_YET + " : EXECUTE");
  }
}
