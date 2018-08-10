const request = require('sync-request');

/*
 * Helper to perform API calls
 * Documentation: https://www.npmjs.com/package/sync-request
 */
module.exports = {
  post: (url, payload, token) => {

    //console.log('POST: ' + url);
    //console.log('Payload: ' + JSON.stringify(payload));

    return JSON.parse(request('POST', url, {
      headers: {'Authorization': token},
      json: payload
    }).getBody());
  },
  delete: (url, token) => {

    //console.log('POST: ' + url);
    //console.log('Payload: ' + JSON.stringify(payload));

    return JSON.parse(request('DELETE', url, {
      headers: {'Authorization': token}
    }).getBody());
  }
};
