
import org.json4s.JsonAST._
import org.json4s.JsonDSL._

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
    it("with inner join should have a FROM clause with an inner join") {
      query(artifactT, artifactU)(joins(
        join("inner", "t", "a", "u", "c"))
      ) must be ("SELECT t.a, t.b, u.c, u.d FROM t INNER JOIN u ON (t.a = u.c)")
    }
    it("with left join should have a FROM clause with a left join") {
      query(artifactT, artifactU)(joins(
        join("left", "t", "a", "u", "c"))
      ) must be ("SELECT t.a, t.b, u.c, u.d FROM t LEFT JOIN u ON (t.a = u.c)")
    }
    it("with right join should have a FROM clause with a right join") {
      query(artifactT, artifactU)(joins(
        join("right", "t", "a", "u", "c"))
      ) must be ("SELECT t.a, t.b, u.c, u.d FROM t RIGHT JOIN u ON (t.a = u.c)")
    }
    it("with multiple joins should have a FROM clause with chained joins") {
      query(artifactT, artifactU, artifactV)(joins(
        join("inner", "t", "a", "u", "c"),
        join("inner", "u", "c", "v", "g"))
      ) must be ("SELECT t.a, t.b, u.c, u.d FROM " +
        "t INNER JOIN u INNER JOIN v ON (t.a = u.c AND u.c = v.g)")
    }
    it("with integer filter should have a WHERE clause with condition") {
      query(artifactT)(filters("AND", filterBinary("int", "t", "a", "gt", "1"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a > 1")
    }
    it("with long filter should have a WHERE clause with condition") {
      query(artifactT)(filters("AND", filterBinary("long", "t", "a", "lt", "1"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a < 1")
    }
    it("with float filter should have a WHERE clause with condition") {
      query(artifactT)(filters("AND", filterBinary("float", "t", "a", "eq", "1.0"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a = 1.0")
    }
    it("with double filter should have a WHERE clause with condition") {
      query(artifactT)(filters("AND", filterBinary("double", "t", "a", "neq", "1.0"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a != 1.0")
    }
    it("with long between filter should have a WHERE clause with BETWEEN") {
      query(artifactT)(filters("AND", filterBinary(
        "long", "t", "a", "btw", "2", "1"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a BETWEEN 1 AND 2")
    }
    it("with string filter should have a WHERE clause with condition") {
      query(artifactT)(filters("AND", filterString(
        "string", "t", "a", "isin", "abc", "def"))
      ) must be ("SELECT t.a, t.b FROM t WHERE upper(t.a) IN ('ABC', 'DEF')")
    }
    it("with date between filter should have a WHERE clause with BETWEEN") {
      query(artifactT)(filters("AND", filterDate(
        "date", "t", "a", "2017-01-01", "2017-01-02"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a >= " +
        "TO_DATE('2017-01-02') AND t.a <= TO_DATE(date_add('2017-01-01', 1))")
    }
    it("with timestamp between filter should have a WHERE clause with BETWEEN") {
      query(artifactT)(filters("AND", filterDate(
        "timestamp", "t", "a", "2017-01-01T00:00:00Z", "2017-01-02T00:00:00Z"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a >= " +
        "TO_DATE('2017-01-02T00:00:00Z') AND t.a <= TO_DATE(date_add('2017-01-01T00:00:00Z', 1))")
    }

   it("with two filters should have a WHERE clause with one OR") {
      query(artifactT)(filters("OR",
        filterBinary("float", "t", "a", "gte", "1"),
        filterBinary("double", "t", "b", "lte", "2"))
      ) must be ("SELECT t.a, t.b FROM t WHERE t.a >= 1 OR t.b <= 2")
    }
    it("with order by columns should have an ORDER BY clause") {
      val orderByA = orderByColumn("t", "a", "ASC")
      val orderByB = orderByColumn("t", "b", "DESC")
      query(artifactT)(orderBy(orderByA, orderByB)
      ) must be ("SELECT t.a, t.b FROM t ORDER BY t.a ASC, t.b DESC")
    }
    it("should only have sqlBuilders dataFields columns in SELECT") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")()("a","b","c")(""))
      ) must be ("SELECT t.a, t.b, t.c FROM t")
    }
  }

  describe("Query built from analysis with group by") {
    it("with sum should have aggregate function in FROM clause") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")("a")("b","c")("SUM"))
      ) must be ("SELECT SUM(t.a), t.b, t.c FROM t GROUP BY t.b, t.c")
    }
    it("with percentage should have aggregate function in FROM clause") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")("a")("b","c")("percentage"))
      ) must be ("SELECT (t.a*100)/(Select sum(t.a) FROM t) as a, t.b, t.c FROM t GROUP BY t.a, t.b, t.c")
    }
    it("with avg should have aggregate function in FROM clause") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")("b")("a")("AVG"))
      ) must be ("SELECT AVG(t.b), t.a FROM t GROUP BY t.a")
    }
    it("with min should have aggregate function in FROM clause") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")("b","c")("a")("MIN"))
      ) must be ("SELECT MIN(t.b), MIN(t.c), t.a FROM t GROUP BY t.a")
    }
    it("with max should have aggregate function in FROM clause") {
      query(artifactT)(sqlBuilderPrep(dataFields("t")("b")()("MAX"))
      ) must be ("SELECT MAX(t.b) FROM t")
    }
    it("with multiple columns should have aggregates in FROM clause") {
      query(artifactW)(sqlBuilderPrep(dataFields("w")("a")("b","c")("AVG"),
        dataFields("w")("d")("e","f")("COUNT"))
      ) must be ("SELECT AVG(w.a), w.b, w.c, COUNT(w.d), w.e, w.f FROM w GROUP BY w.b, w.c, w.e, w.f")
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

  private def artifactW = {
    artifact("w")("g", "h", "i")()
  }

  private def query(artifacts: JObject*)(sqlBuilders: JObject*): String = {
    val sqlBuilderJson = if (sqlBuilders.isEmpty)
      JObject() else sqlBuilders.reduceLeft(_ merge _)
    QueryBuilder.build(("artifacts", artifacts) ~
      ("sqlBuilder", sqlBuilderJson),false, null)
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

  private def filters(bool: String, filters: JObject*): JObject = {
    ("booleanCriteria", bool) ~
    ("filters", filters.toList)
  }

  private def filterCommon(filterType: String, tableName: String,
    columnName: String, operator: String): JObject = {
    val operatorJson: JObject = if (operator != null) {
      ("operator", operator)
    } else {
      JObject()
    }
    ("type", filterType) ~
    ("tableName", tableName) ~
    ("columnName", columnName) ~
    ("model", operatorJson) ~
    ("isRuntimeFilter", false)
  }

  private def filterBinary(filterType: String, tableName: String,
    columnName: String, operator: String, value: String,
    otherValue: String = null): JObject = {
    val otherValueJson: JObject = if (otherValue != null) {
      ("otherValue", otherValue)
    } else {
      JObject()
    }
    filterCommon(filterType, tableName, columnName, operator).merge(
      ("model", ("value", value) ~ otherValueJson): JObject)
  }

  private def filterString(filterType: String, tableName: String,
    columnName: String, operator: String, values: String*): JObject = {
    filterCommon(filterType, tableName, columnName, operator).merge(
      ("model", ("modelValues", JArray(values.map(JString(_)).toList))): JObject)
  }

  private def filterDate(filterType: String, tableName: String,
    columnName: String, lte: String, gte: String): JObject = {
    filterCommon(filterType, tableName, columnName, null).merge(
      ("model", ("lte", lte) ~ ("gte", gte) ~ ("preset","NA")): JObject)
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

  private def sqlBuilderPrep(dataFields: JObject*): JObject = {
    ("dataFields",dataFields)
  }

  private def dataFields(name: String)(aggColumns: String*)
                        (nonAggColumns : String*)(agg:String): JObject = {
    ("tableName", name) ~
      ("columns", aggColumns.map(("columnName", _) ~ ("checked", true)~ ("aggregate",agg)) ++
        nonAggColumns.map(("columnName", _) ~ ("checked", true)))
  }
}
