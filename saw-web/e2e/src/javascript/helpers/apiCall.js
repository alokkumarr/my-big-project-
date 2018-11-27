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
    logger.debug('Api call token :'+token);
    let headers = {};
    if (token) {
      headers['Authorization'] = token;
    }
    let response = request('POST', url, {
      headers: headers,
      json: payload
    });
    if (response.statusCode === 200) {
      let data = JSON.parse(response.getBody());
      logger.debug('post call response-->'+JSON.stringify(data))
      return data;
    } else {
      logger.error('post call failed with status code:'+response.statusCode+' url: '+url+' with response ->'+JSON.stringify(response)+',Hence returning null');
      return null;
    }

  },
  get: (url, token) => {
    logger.info('making get call to url :'+url);
    logger.debug('get call token :'+token);
    let response = request('GET', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200 || response.statusCode === 202) {
      let data = JSON.parse(response.getBody());
      logger.debug('get call response-->'+JSON.stringify(data))
      return data;
    } else {
      logger.error('get call failed with status code:'+response.statusCode+' url: '+url+' with response ->'+JSON.stringify(response)+',Hence returning null');
      return null;
    }
  },
  delete: (url, token) => {
    logger.info('making delete call to url :'+url);
    logger.debug('delete call token :'+token);
    let response = request('DELETE', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200) {
      let data = JSON.parse(response.getBody());
      logger.debug('delete call response-->'+JSON.stringify(data))
      return data;
    } else {
      logger.error('delete call failed with status code:'+response.statusCode+' url: '+url+' with response ->'+JSON.stringify(response)+',Hence returning null');
      return null;
    }
  }
};
