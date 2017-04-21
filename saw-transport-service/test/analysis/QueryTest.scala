import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.parse
import org.scalatest.FunSpec
import org.scalatest.MustMatchers

import model.QueryBuilder

class QueryTest extends FunSpec with MustMatchers {
  describe("Query built from analysis") {
    it("should have SELECT and FROM") {
      query(artifactAB) must be ("SELECT t.a, t.b FROM t")
    }
    it("with filters should have a WHERE clause") {
      query(artifactAB, filters(
        filter("", "t.a", ">", "1"),
        filter("AND", "t.b", "<", "2"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a > 1 AND t.b < 2")
    }
    it("with group by columns should have an GROUP BY clause") {
      query(artifactAB, groupBy("t.a", "t.b")
      ) must be ("SELECT t.a, t.b FROM t GROUP BY t.a, t.b")
    }
    it("with order by columns should have an ORDER BY clause") {
      query(artifactAB, orderBy("t.a ASC", "t.b DESC")
      ) must be ("SELECT t.a, t.b FROM t ORDER BY t.a ASC, t.b DESC")
    }
  }

  private def artifactAB = {
    artifact("t", "a", "b")
  }

  private def query(objs: JObject*): String = {
    val artifact = objs.reduceLeft(_ merge _)
    val analysis = ("artifacts", List(artifact))
    QueryBuilder.build(analysis)
  }

  private def artifact(name: String, columns: String*): JObject = {
    ("artifact_name", name) ~
    ("artifact_attributes", columns.map(("column_name", _)))
  }

  private def filters(filters: JObject*): JObject = {
    ("filters", filters.toList)
  }

  private def filter(bool: String, name: String, operator: String,
    cond: String): JObject = {
    ("boolean_criteria", bool) ~
    ("column_name", name) ~
    ("operator", operator) ~
    ("search_conditions", cond)
  }

  private def orderBy(name: String*) = {
    ("order_by_columns", name)
  }

  private def groupBy(name: String*) = {
    ("group_by_columns", name)
  }
}
