'use strict';
const urlParser = require('url');
const Constants = require('../../../v2/helpers/Constants');

class ApiUtils {
  getHost(baseUrl) {
    const q = urlParser.parse(baseUrl, true);
    let url = Constants.HTTP_PROTOCOL + '://' + q.host + '/'; // API base url
    return url;
  }
}

module.exports = ApiUtils;
