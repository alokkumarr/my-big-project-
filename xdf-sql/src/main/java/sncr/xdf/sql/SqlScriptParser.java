package sncr.xdf.sql; /**
 * Created by asor0002 on 5/10/2017.
 */
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Find all used tables within an select statement.
 */
public class SqlScriptParser
    implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor, SelectItemVisitor, StatementVisitor {

    private static final String NOT_SUPPORTED_YET = "XDF Spark SQL does not support this statement";
    private List<TableDescriptor> tables;
    private int statementIndex = 0;
    /**
     * There are special names, that are not table names but are parsed as tables. These names are
     * collected here and are not included in the tables - names anymore.
     */
    private List<String> otherItemNames;
    public StatementType stType = StatementType.UNKNOWN;

    /**
     * Main entry for this Tool class. A list of found tables is returned.
     *
     * @param 
     * @return
     */
    public List<TableDescriptor> getTableList(Statement statement, int inx) {
        init();
        statementIndex = inx;
        statement.accept(this);
        return tables;
    }
    
    public void visit(Select select) {
        if (select.getWithItemsList() != null) {
            for (WithItem withItem : select.getWithItemsList()) {
                withItem.accept(this);
            }
        }
        select.getSelectBody().accept(this);
    }

    /**
     * Main entry for this Tool class. A list of found tables is returned.
     */
    public List<TableDescriptor> getTableList(Expression expr) {
        init();
        expr.accept(this);
        return tables;
    }

    /**
     * Initializes table names collector.
     */
    protected void init() {
        otherItemNames = new ArrayList<>();
        tables = new ArrayList<>();
    }
     
    public void visit(WithItem withItem) {
        otherItemNames.add(withItem.getName().toLowerCase());
        withItem.getSelectBody().accept(this);
    }
     
    public void visit(PlainSelect plainSelect) {
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem item : plainSelect.getSelectItems()) {
                item.accept(this);
            }
        }

        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(this);
            }
        }
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
        }
        if (plainSelect.getOracleHierarchical() != null) {
            plainSelect.getOracleHierarchical().accept(this);
        }
        stType = (stType == StatementType.UNKNOWN )?StatementType.SELECT:stType;
    }
     
    public void visit(Table tableName) {
        String tableWholeName = tableName.getFullyQualifiedName();
        if (!otherItemNames.contains(tableWholeName.toLowerCase())
            && !tables.contains(tableWholeName)) {
            tables.add(new TableDescriptor(tableWholeName, false, statementIndex, false));
        }
    }
     
    public void visit(SubSelect subSelect) {
        if (subSelect.getWithItemsList() != null) {
            for (WithItem withItem : subSelect.getWithItemsList()) {
                withItem.accept(this);
            }
        }
        subSelect.getSelectBody().accept(this);
    }
     
    public void visit(Addition addition) {
        visitBinaryExpression(addition);
    }
     
    public void visit(AndExpression andExpression) {
        visitBinaryExpression(andExpression);
    }
     
    public void visit(Between between) {
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);
    }
     
    public void visit(Column tableColumn) {
    }
     
    public void visit(Division division) {
        visitBinaryExpression(division);
    }
     
    public void visit(DoubleValue doubleValue) {
    }
     
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }
     
    public void visit(Function function) {
        ExpressionList exprList = function.getParameters();
        if (exprList != null) {
            visit(exprList);
        }
    }
     
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }
     
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }
     
    public void visit(InExpression inExpression) {
        if (inExpression.getLeftExpression() != null) {
            inExpression.getLeftExpression().accept(this);
        } else if (inExpression.getLeftItemsList() != null) {
            inExpression.getLeftItemsList().accept(this);
        }
        inExpression.getRightItemsList().accept(this);
    }
     
    public void visit(SignedExpression signedExpression) {
        signedExpression.getExpression().accept(this);
    }
     
    public void visit(IsNullExpression isNullExpression) {
    }
     
    public void visit(JdbcParameter jdbcParameter) {
    }
     
    public void visit(LikeExpression likeExpression) {
        visitBinaryExpression(likeExpression);
    }
     
    public void visit(ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
    }
     
    public void visit(LongValue longValue) {
    }
     
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }
     
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }
     
    public void visit(Multiplication multiplication) {
        visitBinaryExpression(multiplication);
    }
     
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

     
    public void visit(NullValue nullValue) {
    }
     
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }
     
    public void visit(Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }
     
    public void visit(StringValue stringValue) {
    }
     
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }
     
    public void visit(NotExpression notExpr) {
        notExpr.getExpression().accept(this);
    }

    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
    }
     
    public void visit(ExpressionList expressionList) {
        for (Expression expression : expressionList.getExpressions()) {
            expression.accept(this);
        }
    }
     
    public void visit(DateValue dateValue) {
    }
     
    public void visit(TimestampValue timestampValue) {
    }
     
    public void visit(TimeValue timeValue) {
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.jsqlparser.expression.ExpressionVisitor#visit(net.sf.jsqlparser.expression.CaseExpression)
     */
     
    public void visit(CaseExpression caseExpression) {
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.jsqlparser.expression.ExpressionVisitor#visit(net.sf.jsqlparser.expression.WhenClause)
     */
     
    public void visit(WhenClause whenClause) {
    }

    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }
     
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }
     
    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        subjoin.getJoin().getRightItem().accept(this);
    }
     
    public void visit(Concat concat) {
        visitBinaryExpression(concat);
    }
     
    public void visit(Matches matches) {
        visitBinaryExpression(matches);
    }
     
    public void visit(BitwiseAnd bitwiseAnd) {
        visitBinaryExpression(bitwiseAnd);
    }
     
    public void visit(BitwiseOr bitwiseOr) {
        visitBinaryExpression(bitwiseOr);
    }
     
    public void visit(BitwiseXor bitwiseXor) {
        visitBinaryExpression(bitwiseXor);
    }
     
    public void visit(CastExpression cast) {
        cast.getLeftExpression().accept(this);
    }
     
    public void visit(Modulo modulo) {
        visitBinaryExpression(modulo);
    }
     
    public void visit(AnalyticExpression analytic) {
    }
     
    public void visit(SetOperationList list) {
        for (SelectBody plainSelect : list.getSelects()) {
            plainSelect.accept(this);
        }
    }
     
    public void visit(ExtractExpression eexpr) {
    }

    public void visit(LateralSubSelect lateralSubSelect) {
        lateralSubSelect.getSubSelect().getSelectBody().accept(this);
    }

     
    public void visit(MultiExpressionList multiExprList) {
        for (ExpressionList exprList : multiExprList.getExprList()) {
            exprList.accept(this);
        }
    }

    public void visit(ValuesList valuesList) {
    }

    public void visit(IntervalExpression iexpr) {
    }

     
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
    }

     
    public void visit(OracleHierarchicalExpression oexpr) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": ORACLE HIERARCHICAL");
    }

    public void visit(RegExpMatchOperator rexpr) {
        visitBinaryExpression(rexpr);
    }

    public void visit(RegExpMySQLOperator rexpr) {
        visitBinaryExpression(rexpr);
    }

    public void visit(JsonExpression jsonExpr) {
    }

    public void visit(JsonOperator jsonExpr) {
    }

    public void visit(AllColumns allColumns) {
    }

    public void visit(AllTableColumns allTableColumns) {
    }

    public void visit(SelectExpressionItem item) {
        item.getExpression().accept(this);
    }

    public void visit(WithinGroupExpression wgexpr) {
    }

    public void visit(UserVariable var) {
    }

    public void visit(NumericBind bind) {
    }

    public void visit(KeepExpression aexpr) {
    }

    public void visit(MySQLGroupConcat groupConcat) {
    }

    public void visit(Delete delete) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": DELETE");
    }

    public void visit(Update update) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": UPDATE");
    }

    public void visit(Insert insert) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": INSERT");
    }

    public void visit(Replace replace) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": REPLACE");
    }

    public void visit(Drop drop) {
        stType = StatementType.DROP_TABLE;
        String tn = drop.getName().getFullyQualifiedName();
        tables.add(new TableDescriptor(tn, statementIndex, true));
    }

    public void visit(Truncate truncate) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": TRUNCATE");
    }

    public void visit(CreateIndex createIndex) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": CREATE INDEX");
    }

    public void visit(CreateTable create) {
        // Process CREATE TABLE statement
        String tn = create.getTable().getFullyQualifiedName();
        boolean isTemp = false;
        if(create.getCreateOptionsStrings() != null
           && ( create.getCreateOptionsStrings().contains("TEMP") ||
                create.getCreateOptionsStrings().contains("temp") ||
                create.getCreateOptionsStrings().contains("TEMPORARY") ||
                create.getCreateOptionsStrings().contains("temporary"))){
            isTemp = true;
        }
        tables.add(new TableDescriptor(tn, isTemp, statementIndex, true));
        stType = StatementType.CREATE;

        // Process sub-select
        if (create.getSelect() != null) {
            create.getSelect().accept(this);
        }
    }

    public void visit(CreateView createView) {

        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": CREATE VIEW");
    }

     
    public void visit(Alter alter) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": ALTER");
    }

     
    public void visit(Statements stmts) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET);
    }

     
    public void visit(Execute execute) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": EXECUTE");
    }

     
    public void visit(SetStatement set) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": SET");
    }

     
    public void visit(RowConstructor rowConstructor) {
        for (Expression expr : rowConstructor.getExprList().getExpressions()) {
            expr.accept(this);
        }
    }

     
    public void visit(HexValue hexValue) {

    }

     
    public void visit(Merge merge) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": MERGE");
    }

     
    public void visit(OracleHint hint) {
    }

     
    public void visit(TableFunction valuesList) {
    }

     
    public void visit(AlterView alterView) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_YET + ": ALTER VIEW");
    }

     
    public void visit(TimeKeyExpression timeKeyExpression) {
    }

     
    public void visit(DateTimeLiteralExpression literal) {

    }
}
