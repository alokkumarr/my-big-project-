'use strict';
const urlParser = require('url');
const users = require('../data-generation/users');
let RestClient = require('./RestClient');
const Constants = require('../Constants');
const logger = require('../../conf/logger')(__filename);

class APICommonHelpers {

  static getApiUrl(baseUrl) {
    const q = urlParser.parse(baseUrl, true);
    let url = 'http://' + q.host; // API base url
    logger.debug('api base url: '+url)
    return url;
  }

  static generateToken(baseUrl, loginId = null, password = null) {
    const payload = {
      masterLoginId: loginId ? loginId : users.masterAdmin.loginId,
      password: password ? password : users.masterAdmin.password
    };
    let apiUrl = `${this.getApiUrl(baseUrl)}${Constants.API_ROUTES.AUTH}`;
    let response = new RestClient().post(apiUrl, payload);
    if(!response) {
      return null;
    }
    return 'Bearer '.concat(response.aToken);
  }
}

module.exports = APICommonHelpers;
