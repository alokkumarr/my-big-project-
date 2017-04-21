package model

import org.json4s._
import org.json4s.JsonAST.JValue

object QueryBuilder {
  implicit val formats = DefaultFormats

  def build(json: JValue): String = {
    val artifact = json \ "artifacts" match {
      case artifacts: JArray =>
        /* TODO: Implement support for multiple artifacts */
        artifacts.arr(0)
      case JNothing => return ""
      case obj => throw new ClientException("Expected array but got: " + obj)
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
    val attributes: List[JValue] = analysis \ "artifact_attributes" match {
      case attributes: JArray => attributes.arr
      case JNothing => throw new ClientException(
        "At least one analysis attribute required")
      case json: JValue => unexpectedElement(json)
    }
    if (attributes.size < 1)
      throw new ClientException("At least one analysis attribute expected")
    "SELECT " + attributes.map(column(_)).mkString(", ")
  }

  private def column(column: JValue) = {
    (column \ "column_name").extract[String]
  }

  private def buildFrom(analysis: JValue) = {
    val table = analysis \ "artifact_name" match {
      case JString(name) => name
      case _ => throw new ClientException("Artifact name not found")
    }
    if (table.trim().length == 0)
      throw new ClientException("Artifact name cannot be empty")
    "FROM %s".format(table)
  }

  private def buildWhere(analysis: JValue): String = {
    analysis \ "filters" match {
      case filters: JArray => buildWhereFilters(filters.arr)
      case JNothing => ""
      case json: JValue => unexpectedElement(json)
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
    val groupBy: List[JValue] = analysis \ "group_by_columns" match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json)
    }
    if (groupBy.isEmpty) {
      ""
    } else {
      "GROUP BY " + groupBy.map(_.extract[String]).mkString(", ")
    }
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
    throw new ClientException("Unexpected element: %s".format(name))
  }
}
