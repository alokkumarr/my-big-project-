package sncr.datalake.exceptions

import org.apache.log4j.Logger

/**
  * Created by srya0001 on 5/19/2017.
  */

class DAException(reasonCode: ErrorCodes.Value, args: String*) extends Throwable{

  protected val m_log: Logger = Logger.getLogger(classOf[DAException])
  protected val prefix: String = "DA"
  protected var msg: String = s"$prefix-${reasonCode.id}, Reason: ${reasonCode.toString}, Args: ${args.mkString("[", "," , "]")}"

  override def getMessage: String = msg
  m_log.error(msg)


}

object ErrorCodes extends Enumeration{

  val DataObjectNotFound =  Value("Node must have DataObject, DataAccess service does not accepts nodes without DataObjects")
  val NodeDoesNotExist =    Value("Node is not loaded/does not exist in MDDB")
  val DataObjectNotLoaded = Value("Cannot load Data Object from MDDB")
  val UnsupportedFormat =   Value("Unsupported data format occurred")
  val InvalidDataObject =   Value("Invalid Data Object")
  val InvalidAnalysisNode = Value("Invalid Analysis Node")
  val NoResultToSave = Value("No Result to save in AnalysisResult table")
  val IncorrectExecutionCall = Value("Incorrect call of Execution utility")

  val messages: Map[Int, String] = Map(
    NodeDoesNotExist.id -> NodeDoesNotExist.toString
  )




}
