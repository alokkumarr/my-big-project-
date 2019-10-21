package com.synchronoss.sip.alert.service.evaluator;

public interface AlertEvaluation {

  Boolean evaluateAlert(String dataPodId, Long requestTime);
}
