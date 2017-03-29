package model

import org.json4s._
import org.json4s.JsonAST.JValue

object QueryBuilder {
  implicit val formats = DefaultFormats

  def build(json: JValue): String = {
    val artifact = json \ "artifacts" match {
      case artifacts: JArray =>
        if (artifacts.arr.length != 1) {
          throw new QueryException("Expected exactly one artifact")
        }
        artifacts.arr(0)
      case JNothing => return ""
      case obj => throw new QueryException("Expected array but got: " + obj)
    }
    "%s %s %s %s %s".format(
      buildSelect(artifact),
      buildFrom(artifact),
      buildWhere(artifact),
      buildGroupBy(artifact),
      buildOrderBy(artifact)
    ).replaceAll("\\s+", " ").trim
  }

  private def buildSelect(analysis: JValue) = {
    "SELECT 1"
  }

  private def buildFrom(analysis: JValue) = {
    val table = analysis \ "artifact_name" match {
      case JString(name) => name
      case _ => throw new QueryException("Artifact name not found")
    }
    if (table.trim().length == 0)
      throw new QueryException("Artifact name cannot be empty")
    "FROM %s".format(table)
  }

  private def buildWhere(analysis: JValue): String = {
    analysis \ "filters" match {
      case filters: JArray => buildWhereFilters(filters.arr)
      case JNothing => ""
      case json: JValue => throw new QueryException(
        "Unexpected element: %s".format(json.getClass.getSimpleName))
    }
  }

  private def buildWhereFilters(filters: List[JValue]) = {
    if (filters.isEmpty) {
      ""
    } else {
      "WHERE " + filters.map(filter(_)).mkString(" ")
    }
  }

  private def filter(filter: JValue): String = {
    def property(name: String) = {
      (filter \ name).extract[String]
    }
    "%s %s %s %s".format(
      property("boolean_criteria"),
      property("column_name"),
      property("operator"),
      property("search_conditions")
    )
  }

  private def buildGroupBy(analysis: JValue) = {
    ""
  }

  private def buildOrderBy(analysis: JValue) = {
    val orderBy: List[JValue] = analysis \ "order_by_columns" match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json)
    }
    if (orderBy.isEmpty) {
      ""
    } else {
      "ORDER BY " + orderBy.map(_.extract[String]).mkString(", ")
    }
  }

  private def unexpectedElement(json: JValue): Nothing = {
    val name = json.getClass.getSimpleName
    throw new QueryException("Unexpected element: %s".format(name))
  }
}
