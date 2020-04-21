package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMapping;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;

public interface SubscriberService {

  NotificationSubscriber addSubscriber(NotificationSubscriber subscriber, String customerCode);

  List<NotificationSubscriber> addAllSubscribers(
      List<NotificationSubscriber> subscribers, String customerCode);

  NotificationSubscriber getSubscriberByIdAndCustCode(String subscriberId,
      String customerCode);

  NotificationSubscriber getSubscriber(String subscriberId);

  List<NotificationSubscriber> getSubscribersById(List<String> subscriberIds);

  List<NotificationSubscriber> getSubscribersByCustomerCode(String customerCode);

  List<NotificationSubscriber> getSubscribersByChannelTypeAndCustomerCode(
      NotificationChannelType channelType, String customerCode);

  List<NotificationSubscriber> getSubscriberByChannelTypeAndChannelValueAndCustomerCode(
      NotificationChannelType channelType, List<String> channelValue, String customerCode);

  List<NotificationSubscriber> getAllSubscribers();

  NotificationSubscriber updateSubscriber(
      String subscriberId, NotificationSubscriber subscriber, String customerCode);

  void deleteSubscriber(String subscriberId);

  void addSubscribersToModule(ModuleSubscriberMappingPayload payload);

  ModuleSubscriberMappingPayload fetchSubscribersForModule(String moduleId, ModuleName moduleName);
}
