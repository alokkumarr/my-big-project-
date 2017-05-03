package model

import org.json4s._
import org.json4s.JsonAST.JValue

object QueryBuilder {
  implicit val formats = DefaultFormats

  def build(json: JValue): String = {
    val artifacts = json \ "artifacts" match {
      case artifacts: JArray => artifacts.arr
      case JNothing => List()
      case obj: JValue => unexpectedElement(obj)
    }
    val sqlBuilder = json \ "sqlBuilder" match {
      case obj: JObject => obj
      case JNothing => JObject()
      case obj: JValue => unexpectedElement(obj)
    }
    "%s %s %s %s %s".format(
      buildSelect(artifacts),
      buildFrom(artifacts),
      buildWhere(sqlBuilder),
      buildGroupBy(sqlBuilder),
      buildOrderBy(sqlBuilder)
    ).replaceAll("\\s+", " ").trim
  }

  private def buildSelect(artifacts: List[JValue]) = {
    val columnElements = artifacts.map((artifact: JValue) => {
      val artifactName = (artifact \ "artifactName").extract[String]
      val columns: List[JValue] = artifact \ "columns" match {
        case columns: JArray => columns.arr
        case json: JValue => unexpectedElement(json)
      }
      if (columns.size < 1)
        throw new ClientException("At least one artifact column expected")
      columns.filter(columnChecked(_)).map(column(artifactName, _)).mkString(", ")
    }).filter(_ != "")
    if (columnElements.isEmpty)
      throw ClientException("Expected at least one checked column")
    "SELECT " + columnElements.mkString(", ")
  }

  private def columnChecked(column: JValue) = {
    (column \ "checked").extractOrElse[Boolean](false) == true
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

  private def buildWhere(sqlBuilder: JObject): String = {
    val joins = (sqlBuilder \ "joins" match {
      case joins: JArray => joins.arr
      case JNothing => List()
      case json: JValue => unexpectedElement(json)
    }).map(buildWhereJoinElement(_))
    val filters = (sqlBuilder \ "filters" match {
      case filters: JArray => filters.arr
      case JNothing => List()
      case json: JValue => unexpectedElement(json)
    }).map(buildWhereFilterElement(_))
    val conditions = (joins ++ filters)
    if (conditions.isEmpty) {
      ""
    } else {
      "WHERE " + conditions.mkString(" ")
    }
  }

  private def buildWhereJoinElement(join: JValue): String = {
    def property(name: String) = {
      (join \ name)
    }
    if (property("type").extract[String] != "inner") {
      throw new RuntimeException(
        "Join type not implemented: " + property("type"))
    }
    val criteria = property("criteria") match {
      case criteria: JArray => criteria.arr
      case value: JValue => unexpectedElement(value)
    }
    if (criteria.length != 2) {
      throw new ClientException(
        "Expected criteria to have exactly two elements: " + criteria)
    }
    "%s.%s = %s.%s".format(
      (criteria(0) \ "tableName").extract[String],
      (criteria(0) \ "columnName").extract[String],
      (criteria(1) \ "tableName").extract[String],
      (criteria(1) \ "columnName").extract[String]
    )
  }

  private def buildWhereFilterElement(filter: JValue): String = {
    def property(name: String) = {
      (filter \ name).extract[String]
    }
    "%s %s.%s %s %s".format(
      property("booleanCriteria"),
      property("tableName"),
      property("columnName"),
      property("operator"),
      property("searchConditions")
    )
  }

  private def buildGroupBy(sqlBuilder: JObject) = {
    val groupBy: List[JValue] = sqlBuilder \ "groupByColumns" match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json)
    }
    if (groupBy.isEmpty) {
      ""
    } else {
      "GROUP BY " + groupBy.map(buildGroupByElement(_)).mkString(", ")
    }
  }

  private def buildGroupByElement(groupBy: JValue): String = {
    def property(name: String) = {
      (groupBy \ name).extract[String]
    }
    "%s.%s".format(
      property("tableName"),
      property("columnName")
    )
  }

  private def buildOrderBy(sqlBuilder: JObject) = {
    val orderBy: List[JValue] = sqlBuilder \ "orderByColumns" match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json)
    }
    if (orderBy.isEmpty) {
      ""
    } else {
      "ORDER BY " + orderBy.map(buildOrderByElement(_)).mkString(", ")
    }
  }

  private def buildOrderByElement(orderBy: JValue): String = {
    def property(name: String) = {
      (orderBy \ name).extract[String]
    }
    "%s.%s %s".format(
      property("tableName"),
      property("columnName"),
      property("order")
    )
  }

  private def unexpectedElement(json: JValue): Nothing = {
    val name = json.getClass.getSimpleName
    throw new ClientException("Unexpected element: %s".format(name))
  }
}
