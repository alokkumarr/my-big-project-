package com.synchronoss.sip.alert.controller;

import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.service.SubscriberService;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscribers")
@ApiResponses(
    value = {
      @ApiResponse(code = 202, message = "Request has been accepted without any error"),
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
      @ApiResponse(
          code = 403,
          message = "Accessing the resource you were trying to reach is forbidden"),
      @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
      @ApiResponse(code = 500, message = "Internal server Error. Contact System administrator")
    })
public class SubscriberController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberController.class);

  @Autowired SubscriberService subscriberService;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<NotificationSubscriber> getAllSubscribers(
      HttpServletRequest request, HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();
    List<NotificationSubscriber> subscribers =
        subscriberService.getSubscribersByCustomerCode(customerCode);

    return subscribers;
  }

  @RequestMapping(value = "/channeltype/{channelType}", method = RequestMethod.GET)
  public List<NotificationSubscriber> getSubscribersByChannelType(
      HttpServletRequest request, HttpServletResponse response, @PathVariable String channelType) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();

    NotificationChannelType channelTypeObj = NotificationChannelType.fromValue(channelType);

    List<NotificationSubscriber> subscribers =
        subscriberService.getSubscribersByChannelTypeAndCustomerCode(channelTypeObj, customerCode);

    return subscribers;
  }

  @RequestMapping(value = "/channeltype/{channelType}/channelvalue/", method = RequestMethod.POST)
  public List<NotificationSubscriber> getSubscribersByChannelTypeAndValue(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("channelType") String channelType,
      @RequestBody List<String> channelValues) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();

    NotificationChannelType channelTypeObj = NotificationChannelType.fromValue(channelType);
    List<NotificationSubscriber> subscribers =
        subscriberService.getSubscriberByChannelTypeAndChannelValueAndCustomerCode(
            channelTypeObj, channelValues, customerCode);

    return subscribers;
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public NotificationSubscriber addSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody NotificationSubscriber notificationSubscriber) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();

    return subscriberService.addSubscriber(notificationSubscriber, customerCode);
  }

  @RequestMapping(value = "/addAll", method = RequestMethod.POST)
  public List<NotificationSubscriber> addAllSubscribers(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody List<NotificationSubscriber> notificationSubscribers) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    String customerCode = ticket.getCustCode();
    return subscriberService.addAllSubscribers(notificationSubscribers, customerCode);
  }

  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.GET)
  public NotificationSubscriber getSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId) {
    NotificationSubscriber subscriber = subscriberService.getSubscriber(subscriberId);

    return subscriber;
  }

  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.PUT)
  public NotificationSubscriber updateSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId,
      @RequestBody NotificationSubscriber subscriber) {
    return subscriberService.updateSubscriber(subscriberId, subscriber);
  }

  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.DELETE)
  public void deleteSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId) {
    subscriberService.deleteSubscriber(subscriberId);
  }
}
