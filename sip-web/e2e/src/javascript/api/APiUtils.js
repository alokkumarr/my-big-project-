'use strict';
const urlParser = require('url');

class ApiUtils {

     getHost(baseUrl) {
        const q = urlParser.parse(baseUrl, true);
        let url = 'http://'+q.host+'/'; // API base url
        return url;
     }
}

module.exports = ApiUtils;