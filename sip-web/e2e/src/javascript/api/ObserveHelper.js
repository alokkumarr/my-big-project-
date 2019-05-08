'use strict';
const apiCall = require('../helpers/apiCall');

class ObserveHelper {

  deleteDashboard(url, token, dashboardId) {

    try {
      let apiUrl = url + 'services/observe/dashboards/'+dashboardId;
      return apiCall.delete(apiUrl, token);
    }catch (e) {
      console.log(e);
    }

  }
}

module.exports = ObserveHelper;
