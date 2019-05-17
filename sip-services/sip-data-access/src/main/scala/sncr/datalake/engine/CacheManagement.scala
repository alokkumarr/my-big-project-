package sncr.datalake.engine

import java.util.{Timer, TimerTask}

import org.apache.log4j.Logger
import sncr.datalake.DLSession

import scala.collection.Set

/**
  * Created by srya0001 on 5/21/2017.
  */
class CacheManagement {

  private val m_log: Logger = Logger.getLogger(classOf[CacheManagement].getName)

  // creating timer task, timer
  lazy val timer : Timer = new Timer
  var task : TimerTask = null

  def init() =
  {
    task = new java.util.TimerTask {
      override def run(): Unit = {
        val keys: Set[String] = DLSession.sessions.keySet
        keys.foreach(k => {
          val s = DLSession.getSession(k)
          if (s.lastUsed < System.currentTimeMillis() - SESSION_EXP_TIME)
            DLSession.sessions.synchronized {
              DLSession.sessions.remove(k)
            }
        })
      }
    }
    // scheduling the task at interval
    timer.schedule(task,WAIT, WAIT)
    m_log debug s"Cache Manager has been initialized"
  }

  val WAIT: Long = 1000*60                // Thread sleep time,
                                          // destructor wait before interrupt the thread
  val SESSION_EXP_TIME: Long = 1000*60*30 // Session expiration time: 30 min

  override def finalize() : Unit =
  {
    task.cancel()
  }

}
