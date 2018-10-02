'use strict';
const apiCall = require('../helpers/apiCall');
const users = require('../data/users');
const subCategories = require('../data/subCategories');
const request = require('sync-request');
const dataSets = require('../data/datasets');
//const url = 'http://localhost/'; // API base url
const activeStatusInd = 1; // shared for all users
const custSysId = 1; // shared for all users
const customerId = 1; // shared for all users
const customerCode = 'SYNCHRONOSS'; // shared for all users
const productId = 1; // shared for all users
const moduleId = 1; // shared for all users
let url = '';
let RequestModel = require('../api/RequestModel');
const Constants = require('../api/Constants');

class AnalysisHelper {

    //analysisType chart
    createPivotChart(url, token,name, description, type) {
        let semanticId = this.getSemanticId(url,dataSets.pivotChart, token); // Get semanticId (dataset ID)
        return this.generateChart(url,semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.createAnalysis, token,name, description, type);
    }
     /**
     * @description Deletes analysis based on id for given customer
     * @param {String} host
     * @param {String} token
     * @param {String} customerCode
     * @param {String} id
     * @returns {Object}
     */
    deletePivotChart(host, token, customerCode, id) {

      let deletePayload = new RequestModel().getAnalyzeDeletePayload(customerCode, id);
      //Make a delete api call
      return apiCall.post(host + 'services/analysis', deletePayload, token);
    }

    deleteAnalysis(host, token, customerCode, id) {

      let deletePayload = new RequestModel().getAnalyzeDeletePayload(customerCode, id);
      //Make a delete api call
      return apiCall.post(host + 'services/analysis', deletePayload, token);
    }
    /**
     *
     * @param {*} url
     * @param {*} token
     * @param {*} name
     * @param {*} description
     * @param {*} analysisType
     */
    createNewAnalysis(url, token, name, description, analysisType, subType, filters = null) {

      if(analysisType === Constants.ES_REPORT) {
        return this.createEsReport(url, token, name, description, analysisType, subType, filters);

      } else if(analysisType === Constants.CHART) {
        return this.createChart(url, token, name, description, analysisType, subType, filters);
      }
      else if(analysisType === Constants.PIVOT) {
        return this.createPivot(url, token, name, description, analysisType, subType, filters);
      }
      else if(analysisType === Constants.REPORT) {
        return this.createReport(url, token, name, description, analysisType, subType, filters);
      }else {
        throw new Error('Invalid analysisType: '+analysisType);
      }

    }
    //TODO:
    updatePivotChart(params) {

    }
    //TODO:
    findOnePivotChart(id) {

    }
    //TODO:
    findAllPivotChart(params) {

    }

    //analysisType = esReport
    createEsReport(url, token,name, description, analysisType, subType, filters = null) {
      let dataSetName = dataSets.pivotChart;
      return this.createAnalysis(url, token,name, description, analysisType, subType, dataSetName, filters);
    }

     //analysisType = chart
     createChart(url, token,name, description, analysisType, subType, filters = null) {
      let dataSetName = dataSets.pivotChart;
      return this.createAnalysis(url, token,name, description, analysisType, subType, dataSetName, filters);
    }
    //analysisType = pivot
    createPivot(url, token,name, description, analysisType, subType, filters = null) {
      let dataSetName = dataSets.pivotChart;
      return this.createAnalysis(url, token,name, description, analysisType, subType, dataSetName, filters);
    }
    //analysisType = report
    createReport(url, token,name, description, analysisType, subType, filters = null) {
      let dataSetName = dataSets.report;
      return this.createAnalysis(url, token,name, description, analysisType, subType, dataSetName, filters);
    }

    createAnalysis(url, token,name, description, analysisType, subType, dataSetName, filters = null) {
       // Get semanticId (dataset ID)
       let semanticId = this.getSemanticId(url,dataSetName, token);
       // Create
       const createPayload = new RequestModel().getAnalysisCreatePayload(semanticId, analysisType, customerCode);
       // Get ID
       const id = apiCall.post(url + 'services/analysis', createPayload, token).contents.analyze[0].id;
       //Update analysis with fields
       let currentTimeStamp = new Date().getTime();
       let user = users.masterAdmin;
       let subCategory = subCategories.createAnalysis;
       let updatePayload;
       let executePayload;

       if(analysisType === Constants.ES_REPORT) {
        updatePayload = new RequestModel().getEsReportBody(customerCode,id,'update',
                                dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
        executePayload = new RequestModel().getEsReportBody(customerCode,id,'execute',
                                dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
       } else if (analysisType === Constants.CHART) {
          updatePayload = new RequestModel().getChartBody(customerCode,id,'update',
                                dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
          executePayload = new RequestModel().getChartBody(customerCode,id,'execute',
                                dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
       } else if (analysisType === Constants.PIVOT) {
        updatePayload = new RequestModel().getPivotBody(customerCode,id,'update',
                              dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
        executePayload = new RequestModel().getPivotBody(customerCode,id,'execute',
                              dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
      } else if (analysisType === Constants.REPORT) {
        updatePayload = new RequestModel().getReportBody(customerCode,id,'update',
                              dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
        executePayload = new RequestModel().getReportBody(customerCode,id,'execute',
                              dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType, filters);
      } else {
        throw new Error('Invalid analysis type: '+ analysisType);
      }
      //Update
       apiCall.post(url + 'services/analysis', updatePayload, token);
       //execute
       let response = apiCall.post(url + 'services/analysis', executePayload, token);
       return response;
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


      generateChart(url,semanticId, dataSetName, user, subCategory, token, name, description, analysisType, subType) {
        // Create chart
        const createPayload = new RequestModel().getAnalysisCreatePayload(semanticId, analysisType, customerCode);
        // Get chart ID
        const chartID = apiCall.post(url + 'services/analysis', createPayload, token).contents.analyze[0].id;
        //Update charts with fields
        let currentTimeStamp = new Date().getTime();
        const updatePayload = new RequestModel().getPayloadPivotChart(customerCode,chartID,'update',
                                        dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType);
        apiCall.post(url + 'services/analysis', updatePayload, token);
        //Execute the analysis
        const executePayload = new RequestModel().getPayloadPivotChart(customerCode,chartID,'execute',
                                        dataSetName,semanticId,user.userId,user.loginId,name,description, subCategory.id, currentTimeStamp, analysisType, subType);
        return apiCall.post(url + 'services/analysis', executePayload, token);
    }

};
module.exports = AnalysisHelper;
