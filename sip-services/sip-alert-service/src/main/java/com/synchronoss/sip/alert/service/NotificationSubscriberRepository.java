package com.synchronoss.sip.alert.service;

import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import org.springframework.data.repository.CrudRepository;

public interface NotificationSubscriberRepository
    extends CrudRepository<NotificationSubscriber, String> {
}
