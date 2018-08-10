'use strict';
const apiCall = require('../../javascript/helpers/apiCall');
const users = require('../../javascript/data/users');
const roles = require('../../javascript/data/roles');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
const privileges = require('../../javascript/data/privileges');
const request = require('sync-request');
const globalVariables = require('../../javascript/helpers/globalVariables');
const dataSets = require('../../javascript/data/datasets');
const protractorConf = require('../../../../conf/protractor.conf');
const urlParser = require('url');
//const url = 'http://localhost/'; // API base url
const activeStatusInd = 1; // shared for all users
const custSysId = 1; // shared for all users
const customerId = 1; // shared for all users
const customerCode = 'SYNCHRONOSS'; // shared for all users
const productId = 1; // shared for all users
const moduleId = 1; // shared for all users
let url = '';
let RequestModel = require('../../javascript/api/RequestModel');
const Constants = require('../../javascript/api/Constants');

class ObserveHelper {

  deleteDashboard(url, token, dashboardId) {
    let apiUrl = url + 'services/observe/dashboards/'+dashboardId;
    console.log('apiUrl---'+apiUrl);
    return apiCall.delete(apiUrl, token);
  }
}

module.exports = ObserveHelper;
