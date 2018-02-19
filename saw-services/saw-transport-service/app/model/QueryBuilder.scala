package model

import java.text.SimpleDateFormat

import com.synchronoss.DynamicConvertor
import controllers.BaseController
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods._
import org.apache.log4j._
import org.slf4j.{Logger, LoggerFactory}
import play.mvc.Controller
import play.api.Logger



object QueryBuilder extends {
  implicit val formats = DefaultFormats
  val query_logger = LoggerFactory.getLogger(this.getClass.getName);

 def build(json: JValue, runtime: Boolean = false, DSK: String): String = {
    var whereClause : String =null
    val artifacts = json \ "artifacts" match {
      case artifacts: JArray => artifacts.arr
      case JNothing => List()
      case obj: JValue => unexpectedElement(obj, "array", "artifacts")
    }
    val sqlBuilder = json \ "sqlBuilder" match {
      case obj: JObject => obj
      case JNothing => JObject()
      case obj: JValue => unexpectedElement(obj, "object", "sqlBuilder")
    }

    if (DSK!=null && DSK.nonEmpty) {
      whereClause = "%s %s %s %s %s".format(
        buildSelect(artifacts, sqlBuilder),
        buildFrom(artifacts, sqlBuilder),
        buildWhere(sqlBuilder, runtime, DSK),
        buildGroupBy(artifacts, sqlBuilder),
        buildOrderBy(sqlBuilder)
      ).replaceAll("\\s+", " ").trim
    }
    else {
      whereClause = "%s %s %s %s %s".format(
        buildSelect(artifacts, sqlBuilder),
        buildFrom(artifacts, sqlBuilder),
        buildWhere(sqlBuilder, runtime),
        buildGroupBy(artifacts, sqlBuilder),
        buildOrderBy(sqlBuilder)
      ).replaceAll("\\s+", " ").trim

    }
    whereClause
  }

  private def buildSelect(artifacts: List[JValue], sqlBuilder: JObject) = {
    "SELECT " + buildSelectColumns(artifacts).map(
      columnAggregate(sqlBuilder, _)).mkString(", ")
  }

  def columnAggregate(sqlBuilder: JObject, column: String): String = {
    val groupBy = extractArray(sqlBuilder, "groupByColumns")
    groupBy.find(buildGroupByElement(_) == column) match {
      case Some(groupBy) => {
        val function = (groupBy \ "function").extract[String]
        if (!List("sum", "avg", "min", "max").contains(function)) {
          throw new RuntimeException(
            "Group by function not implemented: " + function)
        }
        "%s(%s)".format(function.toUpperCase, column)
      }
      case None => column
    }
  }

  private def buildSelectColumns(artifacts: List[JValue]) = {
    val columnElements = artifacts.flatMap((artifact: JValue) => {
      val artifactName = (artifact \ "artifactName").extract[String]
      val columns = extractArray(artifact, "columns")
      if (columns.size < 1)
        throw new ClientException("At least one artifact column expected")
      columns.filter(columnChecked(_)).map(column(artifactName, _))
    })
    if (columnElements.isEmpty)
      throw ClientException("Expected at least one checked column")
    columnElements
  }

  private def columnChecked(column: JValue) = {
    (column \ "checked").extractOrElse[Boolean](false) == true
  }

  private def column(artifactName: String, column: JValue) = {
    artifactName + "." + (column \ "columnName").extract[String]
  }

  private def buildFrom(artifacts: List[JValue], sqlBuilder: JObject) = {
    val joins = buildFromJoins(sqlBuilder)
    "FROM " + (if (joins.length > 0) joins else
      buildFromArtifacts(artifacts).mkString(", "))
  }

  private def buildFromJoins(sqlBuilder: JObject): String = {
    extractArray(sqlBuilder, "joins") match {
      case Nil => ""
      case joins: List[JValue] => {
        JoinRelation.toSQL(buildJoinTree(joins.map(JoinRelation(_))))
      }
    }
  }

  private def buildJoinTree(joins: List[JoinRelation]) = {
    joins.reduceLeft((a, b) => {
      val left = b.left.relations.toSet
      val prev = a.relations.toSet
      if (!left.subsetOf(prev)) {
        throw new ClientException(
          "Left side of join must exist in preceding joins: " +
            "%s is not subset of %s".format(left, prev))
      }
      new JoinRelation(a.left, b, a.joinType, a.leftColumn, a.rightColumn)
    })
  }

  private def buildFromArtifacts(artifacts: List[JValue]) = {
    artifacts.filter(isArtifactChecked).map((artifact: JValue) => {
      val table = artifact \ "artifactName" match {
        case JString(name) => name
        case _ => throw new ClientException("Artifact name not found")
      }
      if (table.trim().length == 0)
        throw new ClientException("Artifact name cannot be empty")
      table
    })
  }

  private def isArtifactChecked(artifact: JValue): Boolean = {
      val columns: List[JValue] = artifact \ "columns" match {
        case columns: JArray => columns.arr
        case json: JValue => unexpectedElement(json, "array", "columns")
      }
      columns.filter(columnChecked(_)).length > 0
  }

  private def buildWhere(sqlBuilder: JObject, runtime: Boolean): String = {

    val filters = ((sqlBuilder \ "filters") match {
      case filters: JArray => filters.arr
      case JNothing => List()
      case json: JValue => unexpectedElement(json, "array", "filters")
    }).filter((filter: JValue) => {
       query_logger.trace("isRuntimeFilter value on buildWhere : {}", (filter \ "isRuntimeFilter").extract[Boolean]);
       query_logger.trace("isRuntimeFilter value on buildWhere evaluation : {}", !(filter \ "isRuntimeFilter").extract[Boolean] || runtime);
       query_logger.trace("Runtime parameter value : {}", runtime);
      !(filter \ "isRuntimeFilter").extract[Boolean] || runtime
    }).map(buildWhereFilterElement)
    if (filters.isEmpty) {
      ""
    } else {
      val booleanCriteria = (
        sqlBuilder \ "booleanCriteria").extractOrElse[String]("and").toLowerCase
      if (!List("and", "or").contains(booleanCriteria)) {
        throw new RuntimeException(
          "Unrecognized boolean criteria: " + booleanCriteria)
      }
       query_logger.trace("buildWhere query : {}", filters.mkString(" " + booleanCriteria.toUpperCase + " "))
       "WHERE " + filters.mkString(" " + booleanCriteria.toUpperCase + " ")
    }

  }

  private def buildWhere(sqlBuilder: JObject, runtime: Boolean, DSK :String): String = {
    var finalFilter : String = null
    val filters = ((sqlBuilder \ "filters") match {
      case filters: JArray => filters.arr
      case JNothing => List()
      case json: JValue => unexpectedElement(json, "array", "filters")
    }).filter((filter: JValue) => {
      query_logger.trace("isRuntimeFilter value on buildWhere with DSK : {}", (filter \ "isRuntimeFilter").extract[Boolean]);
      query_logger.trace("isRuntimeFilter value on buildWhere evaluation : {}", !(filter \ "isRuntimeFilter").extract[Boolean] || runtime);
      query_logger.trace("Runtime parameter value : {}", runtime);
      !(filter \ "isRuntimeFilter").extract[Boolean] || runtime
    }).map(buildWhereFilterElement)
    if (filters.isEmpty) {
      if(DSK!=null && DSK.nonEmpty){
        finalFilter = "WHERE " + TransportUtils.buildDSK(DSK)
      } else {
        finalFilter= ""
      }
      finalFilter
    } else {
      val booleanCriteria = (
        sqlBuilder \ "booleanCriteria").extractOrElse[String]("and").toLowerCase
      if (!List("and", "or").contains(booleanCriteria)) {
        throw new RuntimeException(
          "Unrecognized boolean criteria: " + booleanCriteria)
      }
      if(DSK!=null && DSK.nonEmpty){
        finalFilter = "WHERE " + filters.mkString(" " + booleanCriteria.toUpperCase + " ") + " AND " + TransportUtils.buildDSK(DSK)
      }
      else {
        finalFilter = "WHERE " + filters.mkString(" " + booleanCriteria.toUpperCase + " ")
      }
      finalFilter
    }
  }

  private def buildWhereFilterElement(filter: JValue): String = {
    def property(name: String) = {
      (filter \ name).extract[String]
    }
    def subProperty(name: String, subname: String) = {
      (filter \ name \ subname).extract[String]
    }
    val condition = property("type") match {
      case "int" | "long" | "float" | "double" | "integer" => {
        val operator = subProperty("model", "operator").toLowerCase
        val value = subProperty("model", "value")
        if (operator == "btw") {
          val otherValue = subProperty("model", "otherValue")
          "BETWEEN %s AND %s".format(otherValue,value)
        } else {
          val operatorSql = operator match {
            case "gt" => ">"
            case "lt" => "<"
            case "gte" => ">="
            case "lte" => "<="
            case "eq" => "="
            case "neq" => "!="
            case obj => throw new ClientException("Unknown operator: " + obj)
          }
          "%s %s".format(operatorSql, value)
        }
      }
      case "string" => {
        val operator = subProperty("model", "operator").toLowerCase
         val modelValues = ((filter \ "model" \ "modelValues") match {
           case array: JArray => array.arr
           case obj => unexpectedElement(obj, "array", "modelValues")
         }).map(_.extract[String].toUpperCase())
        val stringWhereClause = operator match {
          case "eq" => "= " + modelValues(0)
          case "isin" => "IN (" + modelValues.map("'" + _ + "'").mkString(", ") + ")"
          case "neq" => "<> " + modelValues(0)
          case "isnotin" => "NOT IN (" + modelValues.map("'" + _ + "'").mkString(", ") + ")"
          case "sw" => "like '" + modelValues(0) + "%'"
          case "ew" => "like '%" + modelValues(0) + "'"
          case "contains" => "like '%" + modelValues(0) + "%'"
        }
        stringWhereClause
      }
      case "date" | "timestamp" => {
        var lte :String = null
        var gte : String = null
        val preset = subProperty("model", "preset")
        if (preset !=null && !preset.equals("NA")){
          lte = TransportUtils.dynamicDecipher(preset).getLte();
          gte = TransportUtils.dynamicDecipher(preset).getGte();
        }
        else {
          lte = subProperty("model", "lte")
          gte = subProperty("model", "gte")
        }
        //"BETWEEN TO_DATE('%s') AND TO_DATE('%s')".format(gte, lte)
        ">= TO_DATE('%s') AND %s.%s <= TO_DATE('%s')".format(gte, property("tableName"), property("columnName"), lte)
      }
      case obj: String => throw ClientException("Unknown filter type: " + obj)
    }
    if(property("type")=="string") {
      "upper(%s.%s) %s".format(property("tableName"), property("columnName"), condition)
    } else {
      "%s.%s %s".format(property("tableName"), property("columnName"), condition)
    }
  }

  private def buildGroupBy(
    artifacts: List[JValue], sqlBuilder: JObject): String = {
    val groupBy = extractArray(sqlBuilder, "groupByColumns")
    if (groupBy.isEmpty) {
      ""
    } else {
      val selectColumns = buildSelectColumns(artifacts).toSet
      val groupByColumns = groupBy.map(buildGroupByElement(_)).toSet
      "GROUP BY " + (selectColumns -- groupByColumns).mkString(", ")
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
      case json: JValue => unexpectedElement(json, "array", "orderByColumns")
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

  def extractArray(json: JValue, name: String): List[JValue] = {
    json \ name match {
      case l: JArray => l.arr
      case JNothing => List.empty
      case json: JValue => unexpectedElement(json, "array", name)
    }
  }

  def unexpectedElement(
    json: JValue, expected: String, location: String): Nothing = {
    val name = json.getClass.getSimpleName
    throw new ClientException(
      "Unexpected element: %s, expected %s, at %s".format(
        name, expected, location))
  }
}



trait Relation {
  def relations: List[String]
  def joinTerms: List[String]
  def conditions: List[String]
}

case class TableRelation(name: String) extends Relation {
  override def relations = List(name)
  override def joinTerms = List(name)
  override def conditions = List()
}

case class JoinRelation(
  left: Relation, right: Relation, joinType: String,
  leftColumn: String, rightColumn: String) extends Relation {
  override def relations = {
    left.relations ++ right.relations
  }

  override def joinTerms = {
    val join = joinType.toUpperCase + " JOIN"
    left.joinTerms ++ List(join) ++ right.joinTerms
  }

  override def conditions = {
    val condition = "%s = %s".format(leftColumn, rightColumn)
    left.conditions ++ List(condition) ++ right.conditions
  }
}

object JoinRelation {
  implicit val formats = DefaultFormats

  def apply(json: JValue): JoinRelation = {
    def property(name: String) = {
      (json \ name)
    }
    val joinType = property("type").extract[String]
    if (!List("inner", "left", "right").contains(joinType)) {
      throw new RuntimeException("Join type not implemented: " + joinType)
    }
    val criteria = property("criteria") match {
      case criteria: JArray => criteria.arr
      case value: JValue => QueryBuilder.unexpectedElement(
        value, "array", "criteria")
    }
    if (criteria.length != 2) {
      throw new ClientException(
        "Expected criteria to have exactly two elements: " + criteria)
    }
    val leftTable = (criteria(0) \ "tableName").extract[String]
    val leftColumn = (criteria(0) \ "columnName").extract[String]
    val rightTable = (criteria(1) \ "tableName").extract[String]
    val rightColumn = (criteria(1) \ "columnName").extract[String]
    new JoinRelation(
      new TableRelation(leftTable), new TableRelation(rightTable),
      joinType, leftTable + "." + leftColumn, rightTable + "." + rightColumn)
  }

  def toSQL(join: JoinRelation) = {
    "%s ON (%s)".format(
      join.joinTerms.mkString(" "), join.conditions.mkString(" AND "))
  }
}
