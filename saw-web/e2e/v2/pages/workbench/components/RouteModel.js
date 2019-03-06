'use strict';

const logger = require('../../../conf/logger')(__filename);
const commonFunctions = require('../../utils/commonFunctions');
const TestConnectivity = require('./TestConnectivity');
class RouteModel extends TestConnectivity {
  constructor() {
    super();
    this._routeNameInput = element(by.css(`[e2e="route-name"]`));
  }

  fillRouteName(name) {
    commonFunctions.fillInput(this._routeNameInput, name);
  }
}
module.exports = RouteModel;
