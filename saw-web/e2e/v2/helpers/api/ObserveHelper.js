'use strict';
let RestClient = require('./RestClient');
let Utils = require('../Utils');
const API_ROUTES = require('../Constants').API_ROUTES;
const AnalysisHelper = require('./AnalysisHelper');
const logger = require('../../conf/logger')(__filename);
class ObserveHelper {
  deleteDashboard(url, token, dashboardId) {
    const apiUrl = url + API_ROUTES.DELETE_DASHBOARD + '/' + dashboardId;
    return new RestClient().delete(apiUrl, token);
  }

  addAnalysisByApi(
    host,
    token,
    name,
    description,
    analysisType,
    subType,
    filters = null
  ) {
    try {
      const createdAnalysis = new AnalysisHelper().createNewAnalysis(
        host,
        token,
        name,
        description,
        analysisType,
        subType,
        filters
      );
      if (!createdAnalysis) {
        return null;
      }
      const analysisId = createdAnalysis.contents.analyze[0].executionId.split(
        '::'
      )[0];

      const analysis = { analysisName: name, analysisId: analysisId };
      return analysis;
    } catch (e) {
      logger.error(e);
    }
  }
}

module.exports = ObserveHelper;
