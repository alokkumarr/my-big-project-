package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationSubscriberRepository
    extends CrudRepository<NotificationSubscriber, String> {

  List<NotificationSubscriber> findByCustomerCodeAndActive(String customerCode, Boolean active);

  List<NotificationSubscriber> findByChannelTypeAndCustomerCodeAndActive(
      NotificationChannelType channelType, String customerCode, Boolean active);

  //  @Query(
//      value = "SELECT NS FROM NOTIFICATION_SUBSCRIBER NS"
//          + " WHERE NS.channelType=:channelType AND NS.channelValue IN (:channelValues)"
//          + " AND NS.CUSTOMER_CODE=:customerCode AND NS.ACTIVE=:active")
  List<NotificationSubscriber> findByChannelTypeAndChannelValueInAndCustomerCodeAndActive(
//      @Param("channelType")
      NotificationChannelType channelType,
//      @Param("channelValues")
      List<String> channelValues,
//      @Param("customerCode")
      String customerCode,
//      @Param("active")
      Boolean active);
}
