'use strict';
let RestClient = require('./RestClient');
let Utils = require('../Utils');
const Constants = require('../Constants');
class ObserveHelper {

  deleteDashboard(url, token, dashboardId) {
      let apiUrl = url + Constants.API_ROUTES.DELETE_DASHBOARD +'/'+ dashboardId;
      return new RestClient().delete(apiUrl, token);
  }
}

module.exports = ObserveHelper;
