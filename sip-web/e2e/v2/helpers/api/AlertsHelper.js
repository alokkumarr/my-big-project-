'use strict';
let RestClient = require('./RestClient');
const Constants = require('../Constants');
const AlertModel = require('./model/AlertModel');
const logger = require('../../conf/logger')(__filename);

class AlertsHelper {
  /**
   * @description Add the alerts for given user.
   * @param {String} host
   * @param {String} token
   * @param {Object} alertObj
   * @returns {Object}
   */
  addAlerts(host, token, name, desc, severity) {
    let alert = new AlertModel().getBasicAlert(name, desc, severity);
    let addAlertResponse = new RestClient().post(
      host + Constants.API_ROUTES.ALERTS,
      alert,
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
    return alertList.alertRuleDetailsList;
  }
  getAlertDetailByName(host, token, name) {
    let alertList = new RestClient().get(
      host + Constants.API_ROUTES.ALERTS,
      token
    );
    if (!alertList) {
      return null;
    }
    let index = alertList.alertRuleDetailsList.findIndex(
      item => item.alertRuleName == name
    );
    if (index >= 0) {
      return alertList.alertRuleDetailsList[index];
    }
    return null;
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
