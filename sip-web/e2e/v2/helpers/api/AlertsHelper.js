'use strict';
let RestClient = require('./RestClient');
const Constants = require('../Constants');
const logger = require('../../conf/logger')(__filename);

class AlertsHelper {
  /**
   * @description Add the alerts for given user.
   * @param {String} host
   * @param {String} token
   * @param {Object} alertObj
   * @returns {Object}
   */
  addAlerts(host, token, alertObj) {
    let addAlertResponse = new RestClient().post(
      host + Constants.API_ROUTES.ALERTS,
      alertObj,
      token
    );

    if (!addAlertResponse) {
      logger.error('adding alerts has been failed, Please check logs');
      return null;
    }

    return addAlertResponse;
  }

  getAlertList(host, token) {
    let alertList = new RestClient().get(
      host + Constants.API_ROUTES.ALERTS,
      token
    );
    if (!alertList) {
      return null;
    }
    return alertList[0].alertRulesSysId;
  }

  deleteAlert(host, token, id) {
    let response = new RestClient().delete(
      host + Constants.API_ROUTES.ALERTS + `/${id}`,
      token
    );
    if (!response) {
      logger.error(`Alerts can not be deleted. Please check logs.`);
    }
  }
}

module.exports = AlertsHelper;
