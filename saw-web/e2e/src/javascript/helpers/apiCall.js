const request = require('sync-request');
const logger = require('../../../v2/conf/logger')(__filename);
/*
 * Helper to perform API calls
 * Documentation: https://www.npmjs.com/package/sync-request
 */
module.exports = {
  post: (url, payload, token) => {

    logger.info('making post call to url :'+url);
    logger.debug('post call payload :'+JSON.stringify(payload));
    logger.debug('post call token :'+token);
    let headers = {};
    if (token) {
      headers['Authorization'] = token;
    }
    let response = request('POST', url, {
      headers: headers,
      json: payload
    });
    if (response.statusCode === 200) {
      return JSON.parse(response.getBody());
    } else {
      logger.error('Api call failed with status code:'+response.statusCode+' url: '+url+' ,Hence exiting this process');
      process.exit(1);
    }

  },
  get: (url, token) => {

    logger.info('making get call to url :'+url);
    logger.debug('get call token :'+token);
    let response = request('GET', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200 || response.statusCode === 202) {
      return JSON.parse(response.getBody());
    } else {
      logger.error('Api call failed with status code:'+response.statusCode+' url: '+url+' ,Hence exiting this process');
      process.exit(1);
    }
  },
  delete: (url, token) => {

    logger.info('making delete call to url :'+url);
    logger.debug('delete call token :'+token);
    let response = request('DELETE', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200) {
      return JSON.parse(response.getBody());
    } else {
      logger.error('Api call failed with status code:'+response.statusCode+' url: '+url+' ,Hence exiting this process');
      process.exit(1);
    }
  }
};
