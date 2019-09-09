package com.synchronoss.saw.alert.service.evaluator;

public interface EvaluatorListener {

  void createIfNotExists(int retries) throws Exception;

  void recieve() throws Exception;
}
