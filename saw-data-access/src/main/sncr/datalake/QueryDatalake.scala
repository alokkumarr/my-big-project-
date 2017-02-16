package sncr.datalake

import org.apache.log4j.Logger
import org.apache.spark.sql.SQLContext

/**
  * Created by srya0001 on 2/10/2017.
  */
class QueryDatalake(val sqlctx: SQLContext) {
  private val m_log: Logger = Logger.getLogger(classOf[QueryDatalake].getName)

  def run( location: String) : String =
  {
      val df = sqlctx.read.load(location)
      df.printSchema()
      println("Row in SRC: " +  df.count)
      df.show(10)
      "success"
  }



}
