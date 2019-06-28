package sncr.datalake

import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import files.HFileOperations
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.storage.StorageLevel
import org.json4s.JsonAST.{JString, _}
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import sncr.datalake.engine.CacheManagement
import sncr.datalake.exceptions.{DAException, ErrorCodes}
import sncr.metadata.engine.ProcessingResult

import scala.collection.mutable
import scala.reflect.io.File

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
  // Get spark session from DLConfiguration to avoid wicked executor
  // as there are possibilities in case fetching the session on-demand
  // may lead new context.
  lazy val sparkSession = DLConfiguration.getSparkSession

   /* ToDo:: Below spark listener doesn't work with spark 2.1 to find the records count.
     As per spark community, new listeners added in spark 2.2 and same needs to be
     validated while spark version upgrade */

  /* var recordsWrittenCount = 0l*/
 /* sparkSession.sparkContext.addSparkListener(new SparkListener() {
    override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {
      synchronized {
        recordsWrittenCount += taskEnd.taskMetrics.outputMetrics.recordsWritten
      }
    }
  })*/


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
  def loadObject(name: String, location: String, format: String, limit:Int = DLConfiguration.rowLimit) : Unit  = {
    if (!sparkSession.catalog.tableExists("name")) {
    //Recycling
      m_log trace s"Load object 1: $name at location: $location, sample size: $limit"
    if (loadedData.get(name).isDefined) loadedData -= name
    m_log trace s"Load object 2: $name at location: $location, sample size: $limit"
    if (nativeloadedData.get(name).isDefined) nativeloadedData -= name
    m_log trace s"Load object 3: $name at location: $location, sample size: $limit"
    //Reload/load
    val df = format match {
      case "parquet" => sparkSession.read.parquet(location);
      case "json" => sparkSession.read.json(location)
      case "cvs" => sparkSession.read.csv(location)
      case "ndjson" => sparkSession.read.json(location)
      case _ => throw new DAException(ErrorCodes.UnsupportedFormat, format)
    }
    /* Note: Preloading of data objects into driver memory disabled for
     * now to avoid running out of memory. */
    //val data = DLSession.convert(df, limit)
    m_log trace s"Before creating createOrReplaceTempView: $name at location: $location, sample size: $limit"
    df.createOrReplaceTempView(name)
    //loadedData += (name -> data)
    m_log.trace("loadObject name: " + name)
    nativeloadedData += (name -> df)
  }
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
    * BUT it does not load data to data cache
    *
    * @param viewName - temp view/table name
    * @param sql  - statement to execute
    * @return - result indicator
    */
   def execute(viewName: String, sql: String) : (Integer, String) = {
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
   def execute(viewName: String, sql: String,limit : Integer) : (Integer, String) = {
    try{
      m_log info s"Execute SQL: $sql, view name: $viewName"
      val newSql = createSQlWithLimit(sql,limit)
      m_log.info("Onetime/Preview sql : "+newSql)
      val newDf = sparkSession.sql(newSql)
      m_log.info("execute viewName :" + viewName)
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
    m_log.trace("saveData doName: " + doName)
    val df1 = nativeloadedData(doName)
    val df = df1.coalesce(1)
    m_log.trace("saveData format: " + format)
    m_log.trace("saveData location: " + location)
    format match {
      case "parquet" => df.write.parquet(location); (ProcessingResult.Success.id, "Data have been successfully saved as parquet file")
      case "json" =>
        /** Workaround : Spark ignores the null values fields while writing Datasets into JSON files.
          * convert the null values into default values for corresponding data type
          * (For example : String as "", int/double as 0 and 0.0 etc.) to preserve column with null values. */
        /**
          * Dataframe is persisted to avoid revaluation, this can be removed once
          * logic moved to spark listener implementation, since with current spark 2.1 version
          * spark listener doesn't work as expected */
        val dfWithDefaultValue = df.na.fill("").na.fill(0).persist(StorageLevel.MEMORY_AND_DISK_2)
        dfWithDefaultValue.write.json(location);
        m_log.info("recordCount: " + dfWithDefaultValue.count())
        DLSession.createRecordCount(location,dfWithDefaultValue.count())
        // unpersist the dataframe to release memory.
        dfWithDefaultValue.unpersist()
        sparkSession
        (ProcessingResult.Success.id, "Data have been successfully saved as json file")
      case _ =>  (ProcessingResult.Success.id,  ErrorCodes.UnsupportedFormat + ": " + format )

    }
  }

  import scala.collection.JavaConversions._

  /**
    * Cleanup: removed all loaded objects, closes Spark session.
    */
 // override def finalize() : Unit = { close; /* super.finalize() */ }

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
                 // This has been commented due to version incompatibility issue
                 //case "LongType" => JLong(m.get(k)._2.asInstanceOf[Long])
                 case "LongType" => JInt(java.math.BigInteger.valueOf(m.get(k)._2.asInstanceOf[Long]))
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

}

/**
  * Provides:
  * - static methods to convert data from Scala to Java collections
  * - methods to work with Session cache (currently not used).
  *
  */
object DLSession
{
  def createRecordCount(location :String , recordCount:Long ): Unit = {
    var mapper = new ObjectMapper()
    var node = mapper.createObjectNode()
    var os = HFileOperations.writeToFile(location+ File.separator+"_recordCount")
    node.put("recordCount",recordCount)
    os.write(mapper.writeValueAsBytes(node))
    os.flush()
    os.close()
  }

  val sessions: mutable.LinkedHashMap[String, DLSession] = new mutable.LinkedHashMap()
  lazy val cManager = new CacheManagement

  def pinToCache[T<:DLSession](session: T) : Unit = this.synchronized {cManager.init;  sessions(session.getId) = session }
  def removeFromCache(sessionId: String ): Unit = this.synchronized{sessions -= sessionId}
  def getSession[T<:DLSession](sessionId: String ): DLSession = sessions(sessionId)

}
