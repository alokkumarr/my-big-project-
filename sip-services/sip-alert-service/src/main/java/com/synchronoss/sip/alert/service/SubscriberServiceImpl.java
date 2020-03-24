package com.synchronoss.sip.alert.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.Subscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  @Override
  public void addSubscribers(List<NotificationSubscriber> subscribers) {
    MaprConnection connection = new MaprConnection(basePath, notificationSubscribersTable);
    subscribers.stream()
        .forEach(
            subscriber -> {
              String subscriberId = UUID.randomUUID().toString();

              subscriber.setSubscriberId(subscriberId);

              connection.insert(subscriberId, subscriber);
            });
  }

  @Override
  public Subscriber addSubscriber(NotificationSubscriber subscriber) {
    return null;
  }

  @Override
  public Subscriber getSubscriber(String subscriberId) {
    Subscriber subscriber = null;
    return subscriber;
  }

  @Override
  public List<Subscriber> getAllSubscribers() {
    List<Subscriber> subscribers = new ArrayList<>();

    return subscribers;
  }

  @Override
  public Subscriber updateSubscriber(String subscriberId) {
    Subscriber subscriber = null;

    return subscriber;
  }

  @Override
  public void deleteSubscriber(String subscriberId) {
    MaprConnection connection = new MaprConnection(basePath, notificationSubscribersTable);
    boolean isDeleted = connection.deleteById(subscriberId);
  }
}
