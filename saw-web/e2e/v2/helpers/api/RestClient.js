'use strict';
const request = require('sync-request');

class RestClient {
  post(url, payload, token) {
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
      console.log(
        url +
          ': API failed with status code ' +
          response.statusCode +
          ' hence marking test suite failure'
      );
      process.exit(1);
    }
  }

  get(url, token) {
    let response = request('GET', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200 || response.statusCode === 202) {
      return JSON.parse(response.getBody());
    } else {
      console.log(
        url +
          ': API failed with status code ' +
          response.statusCode +
          ' hence marking test suite failure'
      );
      process.exit(1);
    }
  }

  delete(url, token) {
    let response = request('DELETE', url, {
      headers: { Authorization: token }
    });

    if (response.statusCode === 200) {
      return JSON.parse(response.getBody());
    } else {
      console.log(
        url +
          ': API failed with status code ' +
          response.statusCode +
          ' hence marking test suite failure'
      );
      process.exit(1);
    }
  }
}

module.exports = RestClient;
