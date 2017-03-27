package model

import org.json4s._
import org.json4s.JsonAST.JValue

object QueryBuilder {
  def build(json: JValue): String = {
    val artifact = json \ "artifacts" match {
      case artifacts: JArray =>
        if (artifacts.arr.length != 1) {
          throw new QueryException("Expected exactly one artifact")
        }
        artifacts.arr(0)
      case _ => throw new QueryException("Expected array")
    }
    "%s %s %s %s %s".format(
      buildSelect(artifact),
      buildFrom(artifact),
      buildWhere(artifact),
      buildGroupBy(artifact),
      buildOrderBy(artifact)
    ).replaceAll("\\s+", " ").trim
  }

  def buildSelect(analysis: JValue) = {
    "SELECT 1"
  }

  def buildFrom(analysis: JValue) = {
    val table = analysis \ "artifact_name" match {
      case JString(name) => name
      case _ => throw new QueryException("Artifact name not found")
    }
    if (table.trim().length == 0)
      throw new QueryException("Artifact name cannot be empty")
    "FROM %s".format(table)
  }

  def buildWhere(analysis: JValue) = {
    val list = analysis \ "filters" match {
      case JArray(list) => list
      case JNothing => ""
      case json: JValue => throw new QueryException(
        "Unexpected element: %s".format(json.getClass.getSimpleName))
    }
    ""
  }

  def buildGroupBy(analysis: JValue) = {
    ""
  }

  def buildOrderBy(analysis: JValue) = {
    ""
  }
}
