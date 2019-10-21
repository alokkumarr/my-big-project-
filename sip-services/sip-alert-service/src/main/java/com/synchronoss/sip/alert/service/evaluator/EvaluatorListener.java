package com.synchronoss.sip.alert.service.evaluator;

public interface EvaluatorListener {

  void createIfNotExists(int retries) throws Exception;

  void runStreamConsumer() throws Exception;

  boolean sendMessageToStream();
}
