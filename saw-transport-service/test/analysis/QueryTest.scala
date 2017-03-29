import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods.parse
import org.scalatest.FunSpec
import org.scalatest.MustMatchers

import model.QueryBuilder

class QueryTest extends FunSpec with MustMatchers {
  describe("Query built from analysis") {
    it("should have SELECT and FROM") {
      query("t") must be ("SELECT 1 FROM t")
    }
    it("with filters should have a WHERE clause") {
      query("t", filters(
        filter("", "a", ">", "1"),
        filter("AND", "b", "<", "2"))
      ) must be ("SELECT 1 FROM t WHERE a > 1 AND b < 2")
    }
    it("with group by columns should have an GROUP BY clause") {
      query("t", groupBy("a", "b")
      ) must be ("SELECT 1 FROM t GROUP BY a, b")
    }
    it("with order by columns should have an ORDER BY clause") {
      query("t", orderBy("a ASC", "b DESC")
      ) must be ("SELECT 1 FROM t ORDER BY a ASC, b DESC")
    }
  }

  private def query(artifactName: String, objs: JObject*): String = {
    val json: JObject = ("artifact_name", artifactName)
    val artifact = objs.foldLeft(json)(_ merge _)
    val analysis = ("artifacts", List(artifact))
    QueryBuilder.build(analysis)
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
