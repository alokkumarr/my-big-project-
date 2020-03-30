package com.synchronoss.sip.alert.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMappingPayload;
import com.synchronoss.sip.alert.modal.NotificationChannelType;
import com.synchronoss.sip.alert.modal.NotificationSubscriber;
import com.synchronoss.sip.alert.service.SubscriberService;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
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

  /**
   * Get all subscribers API.
   *
   * @param request Request object
   * @param response Response Object
   * @return List of subscribers
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<NotificationSubscriber> getAllSubscribers(
      HttpServletRequest request, HttpServletResponse response) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();
    List<NotificationSubscriber> subscribers =
        subscriberService.getSubscribersByCustomerCode(customerCode);

    return subscribers;
  }

  /**
   * API to fetch all subscribers by channel type.
   *
   * @param request Request object
   * @param response Response Object
   * @param channelType Type of the channel
   * @return List of subscribers
   */
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

  /**
   * API to fetch subscribers by list channel values.
   *
   * @param request Request object
   * @param response Response Object
   * @param channelType Type of the channel
   * @param channelValues List of channel values
   * @return
   */
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

  /**
   * API to add a subscriber.
   *
   * @param request Request object
   * @param response Response object
   * @param notificationSubscriber Subscriber details
   * @return Subscriber details
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public Object addSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody NotificationSubscriber notificationSubscriber) {
    Ticket ticket = SipCommonUtils.getTicket(request);

    String customerCode = ticket.getCustCode();

    NotificationSubscriber subscriber = null;
    try {

      subscriber = subscriberService.addSubscriber(notificationSubscriber, customerCode);
      return subscriber;
    } catch (Exception exception) {
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode node = mapper.createObjectNode();
      node.put("error", "Duplicate email");

      response.setStatus(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR);

      return node;
    }
  }

  /**
   * API to add multiple subscribers.
   *
   * @param request Request object
   * @param response Response object
   * @param notificationSubscribers List of subscriber to be added
   * @return Subscribers added
   */
  @RequestMapping(value = "/addAll", method = RequestMethod.POST)
  public List<NotificationSubscriber> addAllSubscribers(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody List<NotificationSubscriber> notificationSubscribers) {
    Ticket ticket = SipCommonUtils.getTicket(request);
    String customerCode = ticket.getCustCode();
    return subscriberService.addAllSubscribers(notificationSubscribers, customerCode);
  }

  /**
   * API to fetch subscriber by subscriber id.
   *
   * @param request Request object
   * @param response Response object
   * @param subscriberId Subscriber ID
   * @return Subscriber details
   */
  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.GET)
  public NotificationSubscriber getSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId) {
    NotificationSubscriber subscriber = subscriberService.getSubscriber(subscriberId);

    return subscriber;
  }

  /**
   * API to fetch subscribers by subscriber ids.
   *
   * @param request Request object
   * @param response Response object
   * @param subscriberIds List of Subscriber IDs
   * @return Subscriber details
   */
  @RequestMapping(value = "/getSubscribersById", method = RequestMethod.POST)
  public List<NotificationSubscriber> getSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody List<String> subscriberIds) {
    List<NotificationSubscriber> subscribers = subscriberService.getSubscribersById(subscriberIds);

    return subscribers;
  }

  /**
   * API to update a subscriber.
   *
   * @param request Request object
   * @param response Response Object
   * @param subscriberId Subscriber ID
   * @param subscriber Subscriber details
   * @return Updated subscriber details
   */
  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.PUT)
  public NotificationSubscriber updateSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId,
      @RequestBody NotificationSubscriber subscriber) {
    return subscriberService.updateSubscriber(subscriberId, subscriber);
  }

  /**
   * Delete subscriber API.
   *
   * @param request Request object
   * @param response Response object
   * @param subscriberId Subscriber ID
   */
  @RequestMapping(value = "/{subscriberid}", method = RequestMethod.DELETE)
  public void deleteSubscriber(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable("subscriberid") String subscriberId) {
    subscriberService.deleteSubscriber(subscriberId);
  }

  /**
   * API to subscriber to a module.
   *
   * @param request Request object
   * @param response Response Object
   * @param payload Request body
   * @return Mapping details
   */
  @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
  public String subscribeToModule(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestBody ModuleSubscriberMappingPayload payload) {
    subscriberService.addSubscribersToModule(payload);

    return "Mapped successfully";
  }

  /**
   * Fetch subscribers for a given module id and module name.
   *
   * @param request Request object
   * @param response Response Object
   * @param moduleId Module ID
   * @param moduleName Module name (ALERT/ANALYZE)
   * @return Subscriber Details
   */
  @RequestMapping(value = "/getSubscribers", method = RequestMethod.GET)
  public ModuleSubscriberMappingPayload fetchSubscribersForModule(
      HttpServletRequest request,
      HttpServletResponse response,
      @QueryParam("moduleId") String moduleId,
      @QueryParam("moduleName") ModuleName moduleName) {
    ModuleSubscriberMappingPayload payload =
        subscriberService.fetchSubscribersForModule(moduleId, moduleName);

    return payload;
  }
}
