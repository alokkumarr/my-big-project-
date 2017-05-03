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
    it("with filters should have a WHERE clause") {
      query(artifactT)(filters(
        filter("", "t", "a", ">", "1"),
        filter("AND", "t", "b", "<", "2"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a > 1 AND t.b < 2")
    }
    it("with group by columns should have an GROUP BY clause") {
      val groupByA = groupByColumn("t", "a")
      val groupByB = groupByColumn("t", "b")
      query(artifactT)(groupBy(groupByA, groupByB)
      ) must be ("SELECT t.a, t.b FROM t GROUP BY t.a, t.b")
    }
    it("with order by columns should have an ORDER BY clause") {
      val orderByA = orderByColumn("t", "a", "ASC")
      val orderByB = orderByColumn("t", "b", "DESC")
      query(artifactT)(orderBy(orderByA, orderByB)
      ) must be ("SELECT t.a, t.b FROM t ORDER BY t.a ASC, t.b DESC")
    }
  }

  describe("Query built from analysis with multiple artifacts") {
    it("should list all columns in SELECT and all tables in FROM clause") {
      query(artifactT, artifactU)() must be (
        "SELECT t.a, t.b, u.c, u.d FROM t, u")
    }
  }

  private def artifactT = {
    artifact("t", "a", "b")
  }

  private def artifactU = {
    artifact("u", "c", "d")
  }

  private def query(artifacts: JObject*)(sqlBuilders: JObject*): String = {
    val sqlBuilderJson = if (sqlBuilders.isEmpty)
      JObject() else sqlBuilders.reduceLeft(_ merge _)
    QueryBuilder.build(("artifacts", artifacts) ~
      ("sqlBuilder", sqlBuilderJson))
  }

  private def artifact(name: String, columns: String*): JObject = {
    ("artifactName", name) ~
    ("columns", columns.map(("columnName", _)))
  }

  private def filters(filters: JObject*): JObject = {
    ("filters", filters.toList)
  }

  private def filter(bool: String, tableName: String, columnName: String,
    operator: String, cond: String): JObject = {
    ("booleanCriteria", bool) ~
    ("tableName", tableName) ~
    ("columnName", columnName) ~
    ("operator", operator) ~
    ("searchConditions", cond)
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
