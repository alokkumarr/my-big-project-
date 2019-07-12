package com.synchronoss.sip.datalake.engine

import com.synchronoss.sip.datalake.DLConfiguration

/**
  *  This is singleton Object class to create the shared DLSession object.
  */
object DLExecutionObject {
  var dlSessions: DLSession = null
  if (dlSessions == null) {
    DLConfiguration.initSpark()
    dlSessions = new DLSession("SAW-Executor")
  }
}
