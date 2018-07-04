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
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const urlParser = require('url');
//const url = 'http://localhost/'; // API base url
const activeStatusInd = 1; // shared for all users
const custSysId = 1; // shared for all users
const customerId = 1; // shared for all users
const customerCode = 'SYNCHRONOSS'; // shared for all users
const productId = 1; // shared for all users
const moduleId = 1; // shared for all users
let url = '';
let RequestModel = require('./RequestModel');

class AnalysisHelper {

    
    createChart(url, token,name, description, type) {
        let semanticId = this.getSemanticId(url,dataSets.pivotChart, token); // Get semanticId (dataset ID)
        return this.generateChart(url,semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.createAnalysis, token,name, description, type);
    }
    //TODO:
    delete(params) {
        
    }
    //TODO:
    update(params) {
        
    }
    //TODO:
    findOne(id) {
        
    }
    //TODO:
    findAll(params) {
        
    }

    getToken(url) {
        const payload = {'masterLoginId': users.masterAdmin.loginId, 'password': users.masterAdmin.password};
        return 'Bearer '.concat(JSON.parse(request('POST', url + 'security/doAuthenticate',
          {json: payload}).getBody()).aToken);
    }

    getSemanticId(url,dataSetName, token) {
        const payload = {
            'contents': {
            'keys': [{'customerCode': customerCode, 'module': 'ANALYZE'}],
            'action': 'search',
            'select': 'headers',
            'context': 'Semantic'
            }
        };
        let response = apiCall.post(url + 'services/md', payload, token);
        const semanticList = response.contents[0].ANALYZE;
        return this.getValueFromListByKeyValue(semanticList, 'metricName', dataSetName, 'id');
    }

    getValueFromListByKeyValue(list, inputKey, inputValue, getValueOfKey) {
        let returnValue;
      
        for (let i = 0; i < list.length; i++) {
          const data = list[i];
          //console.log(JSON.stringify(list[i]));
      
          // Iterate each item in list
          // If inputValue matches, return value of getValueOfKey from this item in list
          Object.keys(data).forEach(function (key) {
            if (key === inputKey && data[key] === inputValue) {
              returnValue = data[getValueOfKey];
              //console.log('Found! ' + inputKey + ': \'' + data[key] + '\'');
            }
          });
        }
        if (returnValue == null) {
          throw new Error('There is no ' + inputKey + ' in list with value ' + inputValue);
        }
        return returnValue;
      }
    
    generateChart(url,semanticId, dataSetName, user, subCategory, token, name, description, type) {
        // Create chart
        const createPayload = {
          'contents': {
            'keys': [{
              'customerCode': 'SYNCHRONOSS',
              'module': 'ANALYZE',
              'id': semanticId,
              'analysisType': 'chart'
            }], 'action': 'create'
          }
        };
        // Get chart ID
        const chartID = apiCall.post(url + 'services/analysis', createPayload, token).contents.analyze[0].id;
        //Update charts with fields
        let currentTimeStamp = new Date().getTime();
        const updatePayload = new RequestModel().getPayload(customerCode,chartID,'update',
                                        dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, type);
        apiCall.post(url + 'services/analysis', updatePayload, token);
        //Execute the analysis
        const executePayload = new RequestModel().getPayload(customerCode,chartID,'execute',
                                        dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, type);
        return apiCall.post(url + 'services/analysis', executePayload, token);
    }

};
module.exports = AnalysisHelper;