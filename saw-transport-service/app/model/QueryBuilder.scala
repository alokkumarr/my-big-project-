package model

import org.json4s._
import org.json4s.JsonAST.JValue

object QueryBuilder {
  implicit val formats = DefaultFormats

  def build(json: JValue): String = {
    val artifacts = json \ "artifacts" match {
      case artifacts: JArray => artifacts.arr
      case JNothing => return ""
      case obj: JValue => unexpectedElement(obj)
    }
    "%s %s %s %s %s".format(
      buildSelect(artifacts),
      buildFrom(artifacts),
      buildWhere(artifacts(0)),
      buildGroupBy(artifacts(0)),
      buildOrderBy(artifacts(0))
    ).replaceAll("\\s+", " ").trim
  }

  private def buildSelect(artifacts: List[JValue]) = {
    "SELECT " + artifacts.map((artifact: JValue) => {
      val artifactName = (artifact \ "artifactName").extract[String]
      val columns: List[JValue] = artifact \ "columns" match {
        case columns: JArray => columns.arr
        case json: JValue => unexpectedElement(json)
      }
      if (columns.size < 1)
        throw new ClientException("At least one artifact column expected")
      columns.map(column(artifactName, _)).mkString(", ")
    }).mkString(", ")
  }

  private def column(artifactName: String, column: JValue) = {
    artifactName + "." + (column \ "columnName").extract[String]
  }

  private def buildFrom(artifacts: List[JValue]) = {
    "FROM " + artifacts.map((artifact: JValue) => {
      val table = artifact \ "artifactName" match {
        case JString(name) => name
        case _ => throw new ClientException("Artifact name not found")
      }
      if (table.trim().length == 0)
        throw new ClientException("Artifact name cannot be empty")
      table
    }).mkString(", ")
  }

  private def buildWhere(artifacts: JValue): String = {
    artifacts \ "filters" match {
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
      property("booleanCriteria"),
      property("columnName"),
      property("operator"),
      property("searchConditions")
    )
  }

  private def buildGroupBy(artifacts: JValue) = {
    val groupBy: List[JValue] = artifacts \ "groupByColumns" match {
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

  private def buildOrderBy(artifacts: JValue) = {
    val orderBy: List[JValue] = artifacts \ "orderByColumns" match {
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
