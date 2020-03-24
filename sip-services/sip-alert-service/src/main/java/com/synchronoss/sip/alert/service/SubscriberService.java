package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.Subscriber;
import java.util.List;

public interface SubscriberService {
  void addSubscribers(List<NotificationSubscriber> subscribers);

  Subscriber addSubscriber(NotificationSubscriber subscriber);

  Subscriber getSubscriber(String subscriberId);

  List<Subscriber> getAllSubscribers();

  Subscriber updateSubscriber(String subscriberId);

  void deleteSubscriber(String subscriberId);
}
