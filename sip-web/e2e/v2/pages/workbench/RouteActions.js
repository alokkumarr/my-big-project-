'use strict';
const commonFunctions = require('../utils/commonFunctions');
const RouteModel = require('./components/RouteModel');

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
      this.verifyTestConnectivityLogs(data.testConnectivityMessage);
      this.closeTestConnectivity();
    }

    this.setScheduleStartDate();
    this.clickOnCreateRouteBtn();
  }
}

module.exports = RouteActions;
