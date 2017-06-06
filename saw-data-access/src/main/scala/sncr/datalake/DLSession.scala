package sncr.datalake

import java.util
import java.util.UUID
import org.slf4j.{Logger, LoggerFactory}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.json4s.JsonAST.{JString, _}
import org.json4s.native.JsonMethods._
import sncr.datalake.engine.CacheManagement
import sncr.datalake.exceptions.{DAException, ErrorCodes}

import scala.collection.mutable

/**
  * This is base class to be used for all SAW related Spark executors.
  * The class provides basic functions to
  * - load data objects
  * - execute query
  * - save execution result in provided location
  * - get loaded data, data created by execution.
  *
  */
class DLSession(val sessionName: String = "SAW-SQL-Executor") {

  private val m_log: Logger = LoggerFactory.getLogger(classOf[DLSession].getName)
  lazy val id = UUID.randomUUID().toString
  def getId = id
  var lastUsed = System.currentTimeMillis()
  lazy val sparkConf: SparkConf = DLConfiguration.getSparkConfig


  lazy val sparkSession = SparkSession.builder().config(sparkConf)
    .appName("SAW-SQL-Executor::" + (if(sessionName == "unnamed") id else sessionName))
    .getOrCreate()

  /**
    * loadedData - java collection contains ONLY data sample
    */
  private var loadedData : Map[String, java.util.List[java.util.Map[String, (String, Object)]]] = Map.empty

  /**
    * nativeloadedData - scala collection contains native Spark dataset (Dataframe[Row])
    */
  private var nativeloadedData : Map[String, DataFrame] = Map.empty


  /**
    * Load Data Object with name into memory, remove loaded object if it already exists.
    * Please note:
    * - loadedData contains data samples
    * - nativeloadedData
    * @param name - Data Object Name
    * @param location - location to load Data Object from
    * @param format - Format
    * @param limit - Load and convert first limit records.
    * @return
    */
  def loadObject(name: String, location: String, format: String, limit:Int = DLConfiguration.rowLimit) : Unit  =
  {
    //Recycling
    m_log debug s"Load object: $name at location: $location, sample size: $limit"

    if (loadedData.get(name).isDefined) loadedData -= name
    if (nativeloadedData.get(name).isDefined) nativeloadedData -= name

    //Reload/load
    val df = format match {
      case "parquet" => sparkSession.read.parquet(location);
      case "json" => sparkSession.read.json(location)
      case "cvs" =>  sparkSession.read.csv(location)
      case _ =>  throw new DAException(ErrorCodes.UnsupportedFormat, format )
    }
    val data = DLSession.convert(df, limit)
    df.createOrReplaceTempView(name)
    loadedData += (name -> data)
    nativeloadedData += (name -> df)
  }

  def getData(doName : String) : java.util.List[java.util.Map[String, (String, Object)]]
      = { lastUsed = System.currentTimeMillis; loadedData(doName) }

  /**
    * Returns all data - TAKES LONG TIME,  this function materialized data from Dataset
    *
    * @param doName
    * @return
    */
  def getAllData(doName : String) : java.util.List[java.util.Map[String, (String, Object)]] = {
    lastUsed = System.currentTimeMillis
    DLSession.convert(nativeloadedData(doName).collect(), nativeloadedData(doName).dtypes)
  }


  /**
    * Extracts schema from underlying Dataset
    *
    * @param doName - Data Object name
    * @return - returns Data Object schema asJSON
    */
  def getSchema(doName : String) : String = {
    val df = nativeloadedData(doName)
    df.schema.prettyJson
  }

  /**
    * The function executes statement and register it as @param viewName
    *
    * @param viewName - temp view/table name
    * @param sql  - statement to execute
    * @param limit - return max number of rows
    * @return - record set as java Map
    */

  protected def execute(viewName: String, sql: String, limit: Int = DLConfiguration.rowLimit) : Int = {
    try {
      val newDf = sparkSession.sql(sql)
      newDf.createOrReplaceTempView(viewName)

      if (loadedData.get(viewName).isDefined) loadedData -= viewName
      if (nativeloadedData.get(viewName).isDefined) nativeloadedData -= viewName

      val data = DLSession.convert(newDf, DLConfiguration.rowLimit)
      loadedData += (viewName -> data)
      nativeloadedData += (viewName -> newDf)
      lastUsed = System.currentTimeMillis
      0
    }catch {
      case x: Throwable => m_log.error(s"Could not execute SQL for view/object $viewName", x); -1
    }
  }

  /**
    * Save data object with doName into location in given format
    *
    * @param doName - data object name
    * @param location - location
    * @param format - output format: parquet, json
    */
  def saveData(doName : String, location: String, format : String): Unit = {
    val df1 = nativeloadedData(doName)
    val df = df1.coalesce(1)
    format match {
      case "parquet" => df.write.parquet(location)
      case "json" => df.write.json(location)
      case _ =>  throw new DAException(ErrorCodes.UnsupportedFormat, format )
    }
  }


  import scala.collection.JavaConversions._

  /**
    * Cleanup: removed all loaded objects, closes Spark session.
    */
  override def finalize() : Unit = { close; /* super.finalize() */ }

  def close : Unit = {
    loadedData.foreach(  dobj => {dobj._2.foreach( r => r.clear());dobj._2.clear()})
    sparkSession.close
  }

  import scala.collection.JavaConversions._

  /**
    * Convenience method that returns JValue from loaded data sample
    * The method can be used as example how to iterate over returned dataset.
    * @param dobj
    * @return
    */
  def getDataSampleAsJSON(dobj: String) : JValue =
  {
    val data = getData(dobj)
    var i = 0
    JArray(data.map( m => {
      i += 1
      JObject(
        List(JField("rowid", JInt(i)),
          JField("data",
          JObject( m.keySet().map ( k =>
              JField(k, m.get(k)._1 match{
                 case "StringType" =>JString(m.get(k)._2.asInstanceOf[String])
                 case "IntegerType" => JInt(m.get(k)._2.asInstanceOf[Int])
                 case "BooleanType" => JBool(m.get(k)._2.asInstanceOf[Boolean])
                 case "LongType" => JLong(m.get(k)._2.asInstanceOf[Long])
              }) ).toList
          )
          )
        )
      )
    }
    ).toList)
  }

  def getDataSampleAsString(dobj: String) : String = pretty(render(getDataSampleAsJSON(dobj)))


}


/**
  * Provides:
  * - static methods to convert data from Scala to Java collections
  * - methods to work with Session cache (currently not used).
  *
  */
object DLSession
{
  import scala.collection.JavaConversions._
  def convert(rows: Array[Row], dtypes: Array[(String, String)]):java.util.List[util.Map[String, (String, Object)]] = {
    rows.map( r => {
      val dataHashMap = new util.HashMap[String, (String, Object)]()
      val rowTypeAndValue = r.toSeq.zip(dtypes)
      rowTypeAndValue.foreach(colTyped => {
        dataHashMap.put(colTyped._2._1, (colTyped._2._2, colTyped._1.asInstanceOf[Object]))
      })
      dataHashMap
    }).toList
  }

  val sessions: mutable.LinkedHashMap[String, DLSession] = new mutable.LinkedHashMap()
  lazy val cManager = new CacheManagement

  def pinToCache[T<:DLSession](session: T) : Unit = this.synchronized {cManager.init;  sessions(session.getId) = session }
  def removeFromCache(sessionId: String ): Unit = this.synchronized{sessions -= sessionId}
  def getSession[T<:DLSession](sessionId: String ): DLSession = sessions(sessionId)
  def convert(df: DataFrame, limit: Int): util.List[util.Map[String, (String, Object)]] =  convert(df.head(limit), df.dtypes)

  DLConfiguration.initSpark()
}