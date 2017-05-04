import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.parse
import org.scalatest.FunSpec
import org.scalatest.MustMatchers

import model.QueryBuilder

class QueryBuilderTest extends FunSpec with MustMatchers {
  describe("Query built from analysis") {
    it("should have SELECT and FROM") {
      query(artifactT)() must be ("SELECT t.a, t.b FROM t")
    }
    it("should only have checked columns in SELECT") {
      query(artifactU)() must be ("SELECT u.c, u.d FROM u")
    }
    it("should only have tables with checked columns in FROM") {
      query(artifactT, artifactV)(
      ) must be ("SELECT t.a, t.b FROM t")
    }
    it("with joins should have a WHERE clause with join conditions") {
      query(artifactT, artifactU)(joins(
        join("inner", "t", "a", "u", "c"))
      ) must be ("SELECT t.a, t.b, u.c, u.d FROM t, u WHERE t.a = u.c")
    }
    it("with number filter should have a WHERE clause with condition") {
      query(artifactT)(filters(filter("number", "AND", "t", "a", ">", "1"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a > 1")
    }
    it("with string filter should have a WHERE clause with condition") {
      query(artifactT)(filters(filter(
        "string", "AND", "t", "a", null, "abc", "def"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a IN ('abc', 'def')")
    }
    it("with two filters should have a WHERE clause with one AND") {
      query(artifactT)(filters(
        filter("number", "AND", "t", "a", ">", "1"),
        filter("number", "AND", "t", "b", "<", "2"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a > 1 AND t.b < 2")
    }
    it("with order by columns should have an ORDER BY clause") {
      val orderByA = orderByColumn("t", "a", "ASC")
      val orderByB = orderByColumn("t", "b", "DESC")
      query(artifactT)(orderBy(orderByA, orderByB)
      ) must be ("SELECT t.a, t.b FROM t ORDER BY t.a ASC, t.b DESC")
    }
    it("with group by columns should have an GROUP BY clause") {
      val groupByA = groupByColumn("t", "a")
      val groupByB = groupByColumn("t", "b")
      query(artifactT)(groupBy(groupByA, groupByB)
      ) must be ("SELECT t.a, t.b FROM t GROUP BY t.a, t.b")
    }
  }

  describe("Query built from analysis with multiple artifacts") {
    it("should list all columns in SELECT and all tables in FROM clause") {
      query(artifactT, artifactU)() must be (
        "SELECT t.a, t.b, u.c, u.d FROM t, u")
    }
  }

  private def artifactT = {
    artifact("t")("a", "b")()
  }

  private def artifactU = {
    artifact("u")("c", "d")("e", "f")
  }

  private def artifactV = {
    artifact("v")()("g", "h")
  }

  private def query(artifacts: JObject*)(sqlBuilders: JObject*): String = {
    val sqlBuilderJson = if (sqlBuilders.isEmpty)
      JObject() else sqlBuilders.reduceLeft(_ merge _)
    QueryBuilder.build(("artifacts", artifacts) ~
      ("sqlBuilder", sqlBuilderJson))
  }

  private def artifact(name: String)(columns: String*)(
    uncheckedColumns: String*): JObject = {
    ("artifactName", name) ~
    ("columns", columns.map(("columnName", _) ~ ("checked", true)) ++
      uncheckedColumns.map(("columnName", _) ~ ("checked", false)))
  }

  private def joins(joins: JObject*): JObject = {
    ("joins", joins.toList)
  }

  private def join(joinType: String, table1Name: String, column1Name: String,
    table2Name: String, column2Name: String): JObject = {
    ("type", joinType) ~
    ("criteria", JArray(List(
      ("tableName", table1Name) ~ ("columnName", column1Name),
      ("tableName", table2Name) ~ ("columnName", column2Name))))
  }

  private def filters(filters: JObject*): JObject = {
    ("filters", filters.toList)
  }

  private def filter(filterType: String, bool: String, tableName: String,
    columnName: String, operator: String, conditions: String*): JObject = {
    ("filterType", filterType) ~
    ("booleanCriteria", bool) ~
    ("tableName", tableName) ~
    ("columnName", columnName) ~
    ("operator", operator) ~
    ("searchConditions", JArray(conditions.map(JString(_)).toList))
  }

  private def orderBy(columns: JObject*) = {
    ("orderByColumns", columns)
  }

  private def orderByColumn(tableName: String, columnName: String,
    order: String) = {
    ("tableName", tableName) ~
    ("columnName", columnName) ~
    ("order", order)
  }

  private def groupBy(columns: JObject*) = {
    ("groupByColumns", columns)
  }

  private def groupByColumn(tableName: String, columnName: String) = {
    ("tableName", tableName) ~
    ("columnName", columnName)
  }
}
