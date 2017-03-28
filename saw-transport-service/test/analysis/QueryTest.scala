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
  }

  def query(artifactName: String, objs: JObject*): String = {
    val json: JObject = ("artifact_name", artifactName)
    val artifact = objs.foldLeft(json)(_ merge _)
    val analysis = ("artifacts", List(artifact))
    QueryBuilder.build(analysis)
  }

  def filters(filters: JObject*): JObject = {
    ("filters", filters.toList)
  }

  def filter(bool: String, name: String, operator: String, cond: String):
      JObject = {
    ("boolean_criteria", bool) ~
    ("column_name", name) ~
    ("operator", operator) ~
    ("search_conditions", cond)
  }
}
