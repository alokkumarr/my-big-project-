package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;

public interface SubscriberService {
  NotificationSubscriber addSubscriber(NotificationSubscriber subscriber);

  NotificationSubscriber getSubscriber(String subscriberId);

  List<NotificationSubscriber> getAllSubscribers();

  NotificationSubscriber updateSubscriber(String subscriberId);

  void deleteSubscriber(String subscriberId);
}
