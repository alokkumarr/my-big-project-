package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.repository.NotificationSubscriberRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SubscriberServiceImpl implements SubscriberService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberServiceImpl.class);

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Autowired
  private NotificationSubscriberRepository notificationSubscriberRepository;

  @Override
  public NotificationSubscriber addSubscriber(NotificationSubscriber subscriber) {
    String id = UUID.randomUUID().toString();

    // This line can be used when one subscriber with multiple channels is implementeds
    // String subscriberId = UUID.randomUUID().toString();

    subscriber.setId(id);
    subscriber.setSubscriberId(id);

    subscriber.setActive(true);
    subscriber.setCreatedTime(new Date());

    NotificationSubscriber savedSubscriber = notificationSubscriberRepository.save(subscriber);
    return savedSubscriber;
  }

  @Override
  public NotificationSubscriber getSubscriber(String subscriberId) {
    NotificationSubscriber subscriber = null;

    Optional<NotificationSubscriber> subscriberOptional =
        notificationSubscriberRepository.findById(subscriberId);

    if (subscriberOptional.isPresent()) {
      subscriber = subscriberOptional.get();
    }

    return subscriber;
  }

  @Override
  public List<NotificationSubscriber> getSubscribersByCustomerCode(String customerCode) {
    List<NotificationSubscriber> subscribers =
        notificationSubscriberRepository.findByCustomerCode(customerCode);
    return subscribers;
  }

//  @Override
//  public List<NotificationSubscriber> getSubscribersByChannelTypeAndCustomerCode(
//      NotificationChannelType channelType, String customerCode) {
//    List<NotificationSubscriber> subscribers =
//        notificationSubscriberRepository.findByChannelTypeAAndCustomerCode(
//            channelType, customerCode);
//
//    return subscribers;
//  }

  @Override
  public List<NotificationSubscriber> getAllSubscribers() {
    List<NotificationSubscriber> subscribers = new ArrayList<>();

    notificationSubscriberRepository.findAll().forEach(subscribers::add);

    return subscribers;
  }

  @Override
  public NotificationSubscriber updateSubscriber(
      String subscriberId, NotificationSubscriber subscriber) {
    subscriber.setId(subscriberId);
    subscriber.setSubscriberId(subscriberId);

    subscriber.setModifiedTime(new Date());
    notificationSubscriberRepository.save(subscriber);
    return subscriber;
  }

  @Override
  public void deleteSubscriber(String subscriberId) {
    notificationSubscriberRepository.deleteById(subscriberId);
  }
}
