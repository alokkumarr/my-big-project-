'use strict';
let RestClient = require('./RestClient');
let Utils = require('../Utils');
const Constants = require('../Constants');
const AnalysisHelper = require('./AnalysisHelper');
const logger = require('../../conf/logger')(__filename);
class ObserveHelper {

  deleteDashboard(url, token, dashboardId) {
      let apiUrl = url + Constants.API_ROUTES.DELETE_DASHBOARD +'/'+ dashboardId;
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
      let createdAnalysis = new AnalysisHelper().createNewAnalysis(
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
      let analysisId = createdAnalysis.contents.analyze[0].executionId.split(
        '::'
      )[0];

      let analysis = {
        analysisName: name,
        analysisId: analysisId
      };
      return analysis;
    } catch (e) {
      logger.error(e);
    }
}
}

module.exports = ObserveHelper;
