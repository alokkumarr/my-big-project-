'use strict';
const urlParser = require('url');
const users = require('../data-generation/users');
let RestClient = require('./RestClient');
const Constants = require('./Constants');

class APICommonHelpers {

  getAPIURL(baseUrl) {
    const q = urlParser.parse(baseUrl, true);
    let url = 'http://' + q.host; // API base url
    return url;
  }

  token(baseUrl) {
    const payload = {
      masterLoginId: users.masterAdmin.loginId, password: users.masterAdmin.password
    };

    let apiUrl = `${this.getAPIURL(baseUrl)}${Constants.API_ROUTES.AUTH}`;
    console.log(`Auth api url :${apiUrl}`);
    let response = new RestClient().post(apiUrl, payload);
    console.log(response);
    return 'Bearer '.concat(response.aToken);
  }
}

module.exports = APICommonHelpers;
