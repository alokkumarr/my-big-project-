package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NotificationSubscriberRepository
    extends CrudRepository<NotificationSubscriber, String> {

  List<NotificationSubscriber> findByCustomerCode(String customerCode);

//  List<NotificationSubscriber> findByChannelTypeAAndCustomerCode(
//      NotificationChannelType channelType, String customerCode);
}
