package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.Subscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sncr.bda.base.MaprConnection;

@Service
public class SubscriberServiceImpl implements SubscriberService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberServiceImpl.class);

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Value("${sip.service.metastore.notificationSubscribersTable}")
  @NotNull
  private String notificationSubscribersTable;

  @Autowired private NotificationSubscriberRepository notificationSubscriberRepository;

  @Override
  public NotificationSubscriber addSubscriber(NotificationSubscriber subscriber) {
    String id = UUID.randomUUID().toString();
    String subscriberId = UUID.randomUUID().toString();

    subscriber.setId(id);
    subscriber.setSubscriberId(subscriberId);

    NotificationSubscriber savedSubscriber  = notificationSubscriberRepository.save(subscriber);
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
  public List<NotificationSubscriber> getAllSubscribers() {
    List<NotificationSubscriber> subscribers = new ArrayList<>();

    notificationSubscriberRepository.findAll().forEach(subscribers::add);

    return subscribers;
  }

  @Override
  public NotificationSubscriber updateSubscriber(String subscriberId) {
    NotificationSubscriber subscriber = null;

    return subscriber;
  }

  @Override
  public void deleteSubscriber(String subscriberId) {
    notificationSubscriberRepository.deleteById(subscriberId);
  }
}
