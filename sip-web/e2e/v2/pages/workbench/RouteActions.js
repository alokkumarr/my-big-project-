'use strict';
const commonFunctions = require('../utils/commonFunctions');
const RouteModel = require('./components/RouteModel');
const ChannelActions = require('../../pages/workbench/ChannelActions');

class RouteActions extends RouteModel {
  constructor() {
    super();
    // all the page elements in the route list component
  }
  createRoute(routeInfo, testConnectivity = false) {
    this.fillRouteName(routeInfo.routeName);
    this.fillRouteSource(routeInfo.source);
    this.fillRouteFilePattern(routeInfo.filePattern);
    this.fillRouteDestination(routeInfo.destination);
    this.fillRouteBatchSize(routeInfo.batchSize);
    this.fillRouteDescription(routeInfo.desc);
    this.clickOnRouteNextBtn();
    this.clickOnScheduleTab('Hourly');
    this.clickOnFrequency('Hour', 0);
    this.clickOnFrequency('Minute', 2);

    if (testConnectivity) {
      this.clickOnTestConnectivity();
      this.verifyTestConnectivityLogs(testConnectivityMessage);
      this.closeTestConnectivity();
    }

    this.setScheduleStartDate();
    this.clickOnCreateRouteBtn();
  }

  fillRouteInfo(routeInfo, update = false, token = null) {
    this.fillApiRouteName(routeInfo.routeName);
    this.fillApiRouteDestination(routeInfo.destination);
    const channelActions = new ChannelActions();
    channelActions.selectMethodType(routeInfo.method);
    channelActions.fillEndPoint(routeInfo.endPoint);
    if (routeInfo.method === 'POST')
      channelActions.fillRequestBody(JSON.stringify(routeInfo.body));
    const headers = channelActions.updatedHeaders(
      routeInfo.auth,
      routeInfo.headers,
      token
    );
    if (headers) {
      if (update) channelActions.clearHeader();
      channelActions.addHeaders(headers);
    }
    if (routeInfo.queryParams) {
      if (update) channelActions.clearQueryParams();
      channelActions.addQueryParams(routeInfo.queryParams);
    }
    this.fillRouteDescription(routeInfo.desc);
    this.clickOnRouteNextBtn();
    this.clickOnScheduleTab('Hourly');
    this.clickOnFrequency('Hour', 0);
    this.clickOnFrequency('Minute', 2);
  }
}

module.exports = RouteActions;
