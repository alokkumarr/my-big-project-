package com.synchronoss.saw.alert.service.evaluator;

public interface AlertEvaluation {

  Boolean evaluateAlert(String dataPodId, Long requestTime);
}
