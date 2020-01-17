package com.synchronoss.saw.batch.extensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javassist.NotFoundException;

public interface SipRetryContract {

  public void retryFailedJob(Long channelId, Long routeId, String channelType, boolean isDisable,
      String pid, String status, Long jobId) throws NotFoundException;

  public void retryFailedFileTransfer(Long channelId, Long routeId, String fileName,
      boolean isDisable, String source, Long jobId, String channelType);

  default String getBatchId() {
    DateFormat dtFormat = new SimpleDateFormat("MMddyyyyhhmmssSSS");
    return dtFormat.format(new Date()) + Thread.currentThread().getId();
  }
}
