package sncr.datalake

import java.util
import java.util.UUID

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.json4s.JsonAST.{JString, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.CacheManagement
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.metadata.engine.ProcessingResult

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
    * nativeloadedData - scala collection contains native Spark dataset (Dataframe[Row])
    */
  protected var dataIterator : Map[String, java.util.Iterator[java.util.HashMap[String, (String, Object)]]] = Map.empty

  /**
    * Load Data Object with name into memory, remove loaded object if it already exists.
    * Please note:
    * - loadedData contains data samples
    * - nativeloadedData
    *
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
    /* Note: Preloading of data objects into driver memory disabled for
     * now to avoid running out of memory. */
    //val data = DLSession.convert(df, limit)
    df.createOrReplaceTempView(name)
    //loadedData += (name -> data)
    nativeloadedData += (name -> df)
  }

  def getData(doName : String) : java.util.List[java.util.Map[String, (String, Object)]] = {
    lastUsed = System.currentTimeMillis
    val data = loadedData.get(doName)
    if (data.isDefined)
      return data.get
    else
      null
  }


  def getDataset(doName : String) : Dataset[Row] = {
    lastUsed = System.currentTimeMillis
    val data = nativeloadedData.get(doName)
    if (data.isDefined)
      return data.get
    else
      null
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
    * The function executes statement, registers it as @param viewName
    * and load data sample to data cache
    *
    * @param viewName - temp view/table name
    * @param sql  - statement to execute
    * @param limit - return max number of rows
    * @return - result indicator
    */
  protected def executeAndGetData(viewName: String, sql: String, limit: Int = DLConfiguration.rowLimit) : (Integer, String) = {
    try {
      m_log debug s"Execute SQL: $sql, view name: $viewName"
      val newDf = sparkSession.sql(sql)
      newDf.createOrReplaceTempView(viewName)
      if (loadedData.get(viewName).isDefined) loadedData -= viewName
      if (nativeloadedData.get(viewName).isDefined) nativeloadedData -= viewName

      val data = DLSession.convert(newDf, limit)
      loadedData += (viewName -> data)
      nativeloadedData += (viewName -> newDf)
      lastUsed = System.currentTimeMillis
    }
    catch{
      case x: Throwable => {
        val m = s"Could not execute SQL for view/object $viewName"
        m_log.error(m, x)

        return ( ProcessingResult.Error.id, m + ": " + x.getMessage)
      }
    }
    (ProcessingResult.Success.id, "Success")
  }

  /**
    * The function executes statement, registers it as @param viewName
    * BUT it does not load data to data cache
    *
    * @param viewName - temp view/table name
    * @param sql  - statement to execute
    * @return - result indicator
    */
  protected def execute(viewName: String, sql: String) : (Integer, String) = {
    try{
    m_log debug s"Execute SQL: $sql, view name: $viewName"
    val newDf = sparkSession.sql(sql)
    newDf.createOrReplaceTempView(viewName)
    if (nativeloadedData.get(viewName).isDefined) nativeloadedData -= viewName
    nativeloadedData += (viewName -> newDf)
    lastUsed = System.currentTimeMillis
    }
    catch{
      case x: Throwable => {
        val m = s"Could not execute SQL for view/object $viewName"
        m_log.error(m, x)

        return ( ProcessingResult.Error.id, m + ": " + x.getMessage)
      }
    }
    (ProcessingResult.Success.id, "Success")
  }


  /**
    * The function executes statement with row limit, registers it as @param viewName
    * BUT it does not load data to data cache
    *
    * @param viewName - temp view/table name
    * @param sql  - statement to execute
    * @param limit - limit the rows
    * @return - result indicator
    */
  protected def execute(viewName: String, sql: String,limit : Integer) : (Integer, String) = {
    try{
      m_log debug s"Execute SQL: $sql, view name: $viewName"
      val newSql = createSQlWithLimit(sql,limit)
      m_log.debug("Onetime/Preview sql : "+newSql)
      val newDf = sparkSession.sql(newSql)
      newDf.createOrReplaceTempView(viewName)
      if (nativeloadedData.get(viewName).isDefined) nativeloadedData -= viewName
      nativeloadedData += (viewName -> newDf)
      lastUsed = System.currentTimeMillis
    }
    catch{
      case x: Throwable => {
        val m = s"Could not execute SQL for view/object $viewName"
        m_log.error(m, x)

        return ( ProcessingResult.Error.id, m + ": " + x.getMessage)
      }
    }
    (ProcessingResult.Success.id, "Success")
  }


  /**
    * The method materializes Dataset on local machine to Iterator.
    * It could be a preview data (data sample) or
    * It could be entire data file - BE CAREFUL
    * To materialize entire data file - skip limit parameter.
    *
    * @param viewName
    * @param limit
    */
  protected def materializeDataToIterator(viewName: String, limit: Int = 0) : Unit =
  {
    throw new RuntimeException("materializeDataToIterator: No longer supported to prevent out of memory issues")
    if (nativeloadedData.get(viewName).isEmpty) {
      m_log error ("Attempt to materialize non-existing dataset")
      return
    }
    if (dataIterator.get(viewName).isDefined) dataIterator -= viewName
    if (limit > 0)
      dataIterator += ( (viewName) -> convertToIterator(nativeloadedData(viewName).head(limit),nativeloadedData(viewName).dtypes))
    else
      dataIterator += ( (viewName) -> convertToIterator(nativeloadedData(viewName).collect,nativeloadedData(viewName).dtypes))
  }

  /**
    * The method materializes Dataset on local machine as list of Maps.
    * It could be a preview data (data sample) or
    * It could be entire data file - BE CAREFUL
    * To materialize entire data file - skip limit parameter.
    *
    * @param viewName
    * @param limit
    */
  protected def materializeDataToList(viewName: String, limit: Int = DLConfiguration.rowLimit) : Unit = {
    throw new RuntimeException("materializeDataToList: No longer supported to prevent out of memory issues")
    if (nativeloadedData.get(viewName).isEmpty) {
      m_log error "Attempt to materialize non-existing dataset"
      return
    }
    if (loadedData.get(viewName).isDefined) loadedData -= viewName
    val data= if (limit > 0) DLSession.convert(nativeloadedData.get(viewName).get, limit)
    else DLSession.convert(nativeloadedData.get(viewName).get.collect, nativeloadedData.get(viewName).get.dtypes)
    loadedData += (viewName -> data)
  }

  /**
    * Create SQl with limit for performance improvements.
    * @param sql
    * @param limit
    * @return
    */
   private def createSQlWithLimit(sql : String ,limit :Integer): String =
   {
     val tokens = sql.trim.split("\\s+")
    val keyword = tokens.filter(t => t.equalsIgnoreCase("LIMIT"))
     if (keyword==null || keyword.isEmpty)
       sql+" LIMIT "+limit
     else
       sql
   }

  /**
    * Save data object with doName into location in given format
    *
    * @param doName - data object name
    * @param location - location
    * @param format - output format: parquet, json
    */
  def saveData(doName : String, location: String, format : String): (Int, String) = {
    val df1 = nativeloadedData(doName)
    val df = df1.coalesce(1)
    format match {
      case "parquet" => df.write.parquet(location); (ProcessingResult.Success.id, "Data have been successfully saved as parquet file")
      case "json" =>
        /** Workaround : Spark ignores the null values fields while writing Datasets into JSON files.
          * convert the null values into default values for corresponding data type
          * (For example : String as "", int/double as 0 and 0.0 etc.) to preserve column with null values. */
        val dfWithDefaultValue = df.na.fill("").na.fill(0)
        dfWithDefaultValue.write.json(location); (ProcessingResult.Success.id, "Data have been successfully saved as json file")
      case _ =>  (ProcessingResult.Success.id,  ErrorCodes.UnsupportedFormat + ": " + format )
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
    *
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
                 case "DoubleType" => JDouble(m.get(k)._2.asInstanceOf[Double])
              }) ).toList
          )
          )
        )
      )
    }
    ).toList)
  }

  def getDataSampleAsString(dobj: String) : String = pretty(render(getDataSampleAsJSON(dobj)))

  def convertToIterator(rows : Array[Row], dtypes: Array[(String, String)]): java.util.Iterator[util.HashMap[String, (String, Object)]] = {
    throw new RuntimeException("convertToIterator: No longer supported to prevent out of memory issues")
    rows.map( r => {
      val dataHashMap = new util.HashMap[String, (String, Object)]()
      val rowTypeAndValue = r.toSeq.zip(dtypes)
      rowTypeAndValue.foreach(colTyped => {
        dataHashMap.put(colTyped._2._1, (colTyped._2._2, colTyped._1.asInstanceOf[Object]))
      })
      dataHashMap
    }).toIterator
  }


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
    throw new RuntimeException("convert: No longer supported to prevent out of memory issues")
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

  //TODO:: Essentially this call materializes Dataset, it needs to be investigated to choose optimal approach
  //to bing data from datalake
  def convert(df: DataFrame, limit: Int): util.List[util.Map[String, (String, Object)]] = convert(df.head(limit), df.dtypes)

  DLConfiguration.initSpark()
}
