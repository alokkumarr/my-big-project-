package com.synhronoss.sw.workbench.executor.listener;

public interface WorkbenchExecutorListener {


	  void createIfNotExists(int retries) throws Exception;

	  void runWorkbenchConsumer() throws Exception;

	  boolean sendMessageToStream();


}
