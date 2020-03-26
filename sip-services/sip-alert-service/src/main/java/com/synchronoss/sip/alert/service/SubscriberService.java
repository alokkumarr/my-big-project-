package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;

public interface SubscriberService {
  NotificationSubscriber addSubscriber(NotificationSubscriber subscriber, String customerCode);

  List<NotificationSubscriber> addAllSubscribers(
      List<NotificationSubscriber> subscribers, String customerCode);

  NotificationSubscriber getSubscriber(String subscriberId);

  List<NotificationSubscriber> getSubscribersByCustomerCode(String customerCode);

  List<NotificationSubscriber> getSubscribersByChannelTypeAndCustomerCode(
      NotificationChannelType channelType, String customerCode);

  List<NotificationSubscriber> getSubscriberByChannelTypeAndChannelValueAndCustomerCode(
      NotificationChannelType channelType, List<String> channelValue, String customerCode);

  List<NotificationSubscriber> getAllSubscribers();

  NotificationSubscriber updateSubscriber(String subscriberId, NotificationSubscriber subscriber);

  void deleteSubscriber(String subscriberId);
}
