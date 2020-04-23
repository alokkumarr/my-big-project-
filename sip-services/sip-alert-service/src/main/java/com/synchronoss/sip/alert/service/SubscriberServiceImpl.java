package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMapping;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.modal.SubscriberDetails;
import com.synchronoss.sip.alert.repository.ModuleSubscriberMappingRepository;
import com.synchronoss.sip.alert.repository.NotificationSubscriberRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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

  @Autowired private NotificationSubscriberRepository notificationSubscriberRepository;

  @Autowired private ModuleSubscriberMappingRepository moduleSubscriberMappingRepository;

  @Override
  public NotificationSubscriber addSubscriber(
      NotificationSubscriber subscriber, String customerCode) {
    String id = UUID.randomUUID().toString();

    // This line can be used when one subscriber with multiple channels is implementeds
    // String subscriberId = UUID.randomUUID().toString();

    subscriber.setId(id);
    subscriber.setSubscriberId(id);

    subscriber.setCustomerCode(customerCode);
    subscriber.setActive(true);
    subscriber.setCreatedTime(new Date());

    NotificationSubscriber savedSubscriber = notificationSubscriberRepository.save(subscriber);
    return savedSubscriber;
  }

  @Override
  public List<NotificationSubscriber> addAllSubscribers(
      List<NotificationSubscriber> subscribers, String customerCode) {
    List<NotificationSubscriber> result = new ArrayList<>();

    Date date = new Date();
    subscribers =
        subscribers.stream()
            .map(
                subscriber -> {
                  String id = UUID.randomUUID().toString();

                  subscriber.setId(id);
                  subscriber.setSubscriberId(id);
                  subscriber.setCustomerCode(customerCode);
                  subscriber.setActive(true);
                  subscriber.setCreatedTime(date);

                  return subscriber;
                })
            .collect(Collectors.toList());

    notificationSubscriberRepository.saveAll(subscribers);

    return subscribers;
  }

  @Override
  public NotificationSubscriber getSubscriberByIdAndCustCode(String subscriberId,
      String customerCode) {
    return notificationSubscriberRepository
        .findBySubscriberIdAndCustomerCode(subscriberId, customerCode);
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
  public List<NotificationSubscriber> getSubscribersById(List<String> subscriberIds) {
    Boolean active = true;
    return notificationSubscriberRepository.findBySubscriberIdInAndActive(subscriberIds, active);
  }

  @Override
  public List<NotificationSubscriber> getSubscribersByCustomerCode(String customerCode) {
    Boolean active = true;
    List<NotificationSubscriber> subscribers =
        notificationSubscriberRepository.findByCustomerCodeAndActive(customerCode, active);
    return subscribers;
  }

  @Override
  public List<NotificationSubscriber> getSubscribersByChannelTypeAndCustomerCode(
      NotificationChannelType channelType, String customerCode) {
    Boolean active = true;
    List<NotificationSubscriber> subscribers =
        notificationSubscriberRepository.findByChannelTypeAndCustomerCodeAndActive(
            channelType, customerCode, active);

    return subscribers;
  }

  @Override
  public List<NotificationSubscriber> getSubscriberByChannelTypeAndChannelValueAndCustomerCode(
      NotificationChannelType channelType, List<String> channelValues, String customerCode) {
    Boolean active = true;
    List<NotificationSubscriber> subscribers =
        notificationSubscriberRepository.findByChannelTypeAndChannelValueInAndCustomerCodeAndActive(
            channelType, channelValues, customerCode, active);

    return subscribers;
  }

  @Override
  public List<NotificationSubscriber> getAllSubscribers() {
    List<NotificationSubscriber> subscribers = new ArrayList<>();

    notificationSubscriberRepository.findAll().forEach(subscribers::add);

    return subscribers;
  }

  @Override
  public NotificationSubscriber updateSubscriber(
      String subscriberId, NotificationSubscriber subscriber, String customerCode) {
    subscriber.setId(subscriberId);
    subscriber.setSubscriberId(subscriberId);
    subscriber.setCustomerCode(customerCode);

    subscriber.setModifiedTime(new Date());
    notificationSubscriberRepository.save(subscriber);
    return subscriber;
  }

  @Override
  public void deleteSubscriber(String subscriberId) {
    notificationSubscriberRepository.deleteById(subscriberId);
  }

  @Override
  public void addSubscribersToModule(ModuleSubscriberMappingPayload payload) {

    List<ModuleSubscriberMapping> mappingList = new ArrayList<>();

    String moduleId = payload.getModuleId();
    ModuleName moduleName = payload.getModuleName();
    List<SubscriberDetails> subscribers = payload.getSubscribers();

    for (SubscriberDetails subscriber : subscribers) {
      List<NotificationChannelType> channelTypes = subscriber.getChannelTypes();

      for (NotificationChannelType channelType : channelTypes) {
        ModuleSubscriberMapping mapping = new ModuleSubscriberMapping();
        String id = UUID.randomUUID().toString();

        mapping.setId(id);
        mapping.setModuleId(moduleId);
        mapping.setModuleName(moduleName);

        mapping.setAcknowledged(false);
        mapping.setSubscriberId(subscriber.getSubscriberId());
        mapping.setChannelType(channelType);
        mappingList.add(mapping);
      }
    }

    moduleSubscriberMappingRepository.saveAll(mappingList);
  }

  @Override
  public ModuleSubscriberMappingPayload fetchSubscribersForModule(
      String moduleId, ModuleName moduleName) {
    ModuleSubscriberMappingPayload payload = new ModuleSubscriberMappingPayload();
    List<ModuleSubscriberMapping> list =
        moduleSubscriberMappingRepository.findAllByModuleIdAndModuleName(moduleId, moduleName);

    if (list != null && !list.isEmpty()) {
      payload.setModuleId(list.get(0).getModuleId());
      payload.setModuleName(list.get(0).getModuleName());

      payload.setSubscribers(groupSubscribers(list));
    }

    return payload;
  }

  private List<SubscriberDetails> groupSubscribers(List<ModuleSubscriberMapping> list) {
    List<SubscriberDetails> subscriberDetailsList = new ArrayList<>();

    Map<String, List<NotificationChannelType>> subscriberMap = new HashMap<>();

    for (ModuleSubscriberMapping mapping : list) {
      String subscriberId = mapping.getSubscriberId();

      List<NotificationChannelType> channelTypes =
          subscriberMap.getOrDefault(subscriberId, new ArrayList<>());

      channelTypes.add(mapping.getChannelType());

      subscriberMap.put(subscriberId, channelTypes);
    }

    for (Entry<String, List<NotificationChannelType>> entry : subscriberMap.entrySet()) {
      SubscriberDetails details = new SubscriberDetails();
      details.setSubscriberId(entry.getKey());
      details.setChannelTypes(entry.getValue());

      subscriberDetailsList.add(details);
    }

    return subscriberDetailsList;
  }
}
