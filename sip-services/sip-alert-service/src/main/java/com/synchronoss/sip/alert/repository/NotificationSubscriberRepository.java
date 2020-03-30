package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationSubscriberRepository
    extends CrudRepository<NotificationSubscriber, String> {

  List<NotificationSubscriber> findBySubscriberIdInAndActive(
      List<String> subscriberIds, Boolean active);

  List<NotificationSubscriber> findByCustomerCodeAndActive(String customerCode, Boolean active);

  List<NotificationSubscriber> findByChannelTypeAndCustomerCodeAndActive(
      NotificationChannelType channelType, String customerCode, Boolean active);

  List<NotificationSubscriber> findByChannelTypeAndChannelValueInAndCustomerCodeAndActive(
      NotificationChannelType channelType,
      List<String> channelValues,
      String customerCode,
      Boolean active);
}
