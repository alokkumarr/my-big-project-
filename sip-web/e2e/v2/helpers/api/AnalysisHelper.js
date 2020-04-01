'use strict';
let RestClient = require('./RestClient');
const users = require('../data-generation/users');
const categories = require('../data-generation/categories');
const { createSubCategories } = require('../data-generation/subCategories');
const dataSets = require('../data-generation/datasets');
const customerCode = 'SYNCHRONOSS'; // shared for all users
let RequestModel = require('./model/RequestModel');
const Constants = require('../Constants');
let Utils = require('../Utils');
let AdminHelper = require('./AdminHelper');
const logger = require('../../conf/logger')(__filename);

class AnalysisHelper {
  /**
   * @description Deletes analysis based on id for given customer
   * @param {String} host
   * @param {String} token
   * @param {String} customerCode
   * @param {String} id
   * @returns {Object}
   */
  deleteAnalysis(host, token, customerCode, id) {
    // DSL analysis
    const url = `${host}${Constants.API_ROUTES.DSL_ANALYSIS}${id}`;
    return new RestClient().delete(url, token);
  }

  /**
   *
   * @param {*} url
   * @param {*} token
   * @param {*} name
   * @param {*} description
   * @param {*} analysisType
   */
  createNewAnalysis(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    switch (analysisType) {
      case Constants.ES_REPORT:
        return this.createEsReport(
          url,
          token,
          name,
          description,
          analysisType,
          subType,
          filters
        );
      case Constants.CHART:
        return this.createChart(
          url,
          token,
          name,
          description,
          analysisType,
          subType,
          filters
        );
      case Constants.PIVOT:
        return this.createPivot(
          url,
          token,
          name,
          description,
          analysisType,
          subType,
          filters
        );
      case Constants.REPORT:
        return this.createReport(
          url,
          token,
          name,
          description,
          analysisType,
          subType,
          filters
        );
      default:
        return null;
    }
  }

  //TODO:
  updatePivotChart(params) {}

  //TODO:
  findOnePivotChart(id) {}

  //TODO:
  findAllPivotChart(params) {}

  //analysisType = esReport
  createEsReport(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    let dataSetName = dataSets.pivotChart;
    return this.createAnalysis(
      url,
      token,
      name,
      description,
      analysisType,
      subType,
      dataSetName,
      filters
    );
  }

  //analysisType = chart
  createChart(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    let dataSetName = dataSets.pivotChart;
    return this.createAnalysis(
      url,
      token,
      name,
      description,
      analysisType,
      subType,
      dataSetName,
      filters
    );
  }

  //analysisType = pivot
  createPivot(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    let dataSetName = dataSets.pivotChart;
    return this.createAnalysis(
      url,
      token,
      name,
      description,
      analysisType,
      subType,
      dataSetName,
      filters
    );
  }

  //analysisType = report
  createReport(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    let dataSetName = dataSets.report;
    return this.createAnalysis(
      url,
      token,
      name,
      description,
      analysisType,
      subType,
      dataSetName,
      filters
    );
  }

  createAnalysis(
    url,
    token,
    name,
    description,
    analysisType,
    subType,
    dataSetName,
    filters = null,
    subCategory = null,
    semantic = null
  ) {
    // Get if doesn't exist semanticId (dataset ID)
    let semanticId = semantic
      ? semantic
      : this.getSemanticId(url, dataSetName, token);

    if (!semanticId) {
      logger.error('semanticId can not be null, Please check logs');
      return null;
    }

    let currentTimeStamp = new Date().getTime();
    let user = users.masterAdmin;
    let updatePayload;
    let executePayload;
    let subCategoryId;
    let id = null;
    if (subCategory) {
      subCategoryId = subCategory.id;
    } else {
      let cubCatList = new AdminHelper().getSubCategoriesByCategoryName(
        url,
        token,
        categories.analyses.name
      );
      if (createSubCategories) {
        subCategoryId = new AdminHelper().getSubCategoryIdBySubCategoryName(
          cubCatList,
          createSubCategories.createAnalysis.name
        );
      } else {
        logger.error('There are no subcategories found for categories');
        return null;
      }
    }

    if (analysisType === Constants.ES_REPORT) {
      updatePayload = new RequestModel().getEsReportBody(
        customerCode,
        id,
        'update',
        dataSetName,
        semanticId,
        user.userId,
        user.loginId,
        name,
        description,
        subCategoryId,
        currentTimeStamp,
        analysisType,
        subType,
        filters
      );
    } else if (analysisType === Constants.CHART) {
      updatePayload = new RequestModel().getChartBody(
        customerCode,
        id,
        'update',
        dataSetName,
        semanticId,
        user.userId,
        user.loginId,
        name,
        description,
        subCategoryId,
        currentTimeStamp,
        analysisType,
        subType,
        filters
      );
    } else if (analysisType === Constants.PIVOT) {
      updatePayload = new RequestModel().getPivotBody(
        customerCode,
        id,
        'update',
        dataSetName,
        semanticId,
        user.userId,
        user.loginId,
        name,
        description,
        subCategoryId,
        currentTimeStamp,
        analysisType,
        subType,
        filters
      );
    } else if (analysisType === Constants.REPORT) {
      updatePayload = new RequestModel().getReportBody(
        customerCode,
        id,
        'update',
        dataSetName,
        semanticId,
        user.userId,
        user.loginId,
        name,
        description,
        subCategoryId,
        currentTimeStamp,
        analysisType,
        subType,
        filters
      );
    } else {
      logger.info('Invalid analysis type, please check the logs.');
      return null;
    }

    //Update
    if (
      analysisType === Constants.CHART ||
      analysisType == Constants.PIVOT ||
      analysisType == Constants.ES_REPORT ||
      analysisType == Constants.REPORT
    ) {
      const updateResponse = new RestClient().post(
        url + Constants.API_ROUTES.DSL_ANALYSIS,
        updatePayload,
        token
      );
      if (!updateResponse) {
        logger.error(
          'updateResponse dsl analysis api failed, Please check the logs...'
        );
        return null;
      }
      return updateResponse;
    } else {
      const updateResponse = new RestClient().post(
        url + Constants.API_ROUTES.ANALYSIS,
        updatePayload,
        token
      );
      if (updateResponse) {
        //execute
        const executeResponse = new RestClient().post(
          url + Constants.API_ROUTES.ANALYSIS,
          executePayload,
          token
        );
        if (!executeResponse) {
          logger.error('execute analysis api failed, Please check the logs...');
          return null;
        }
        return executeResponse;
      }
    }

    logger.error('update analysis api failed , Please check the logs...');
    return null;
  }

  getSemanticId(url, dataSetName, token) {
    let response = new RestClient().get(
      url + Constants.API_ROUTES.SEMANTIC,
      token
    );
    if (!response) {
      return null;
    }
    const semanticList = response.contents[0].ANALYZE;
    return new Utils().getValueFromListByKeyValue(
      semanticList,
      'metricName',
      dataSetName,
      'id'
    );
  }
}

module.exports = AnalysisHelper;
