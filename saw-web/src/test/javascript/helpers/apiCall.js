const request = require('sync-request');

/*
 * Helper to perform API calls
 * Documentation: https://www.npmjs.com/package/sync-request
 */
module.exports = {
  post: (url, payload, token) => {

    let data = request('POST', url, {
      headers: {'Authorization': token},
      json: payload
    });
    console.log('api response--->'+JSON.stringify(data));

    return JSON.parse(data.getBody());
  },
  delete: (url, token) => {

    return JSON.parse(request('DELETE', url, {
      headers: {'Authorization': token}
    }).getBody());
  }
};
