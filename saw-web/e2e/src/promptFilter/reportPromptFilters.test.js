var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
let AnalysisHelper = require('../javascript/api/AnalysisHelper');
let ApiUtils = require('../javascript/api/APiUtils');
const Constants = require('../javascript/api/Constants');
const globalVariables = require('../javascript/helpers/globalVariables');
const PromptFilterFunctions = require('../javascript/helpers/PromptFilterFunctions');
const chai = require('chai');
const assert = chai.assert;
const logger = require('../../v2/conf/logger')(__filename);
let APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');

describe('Report Prompt filter tests: reportPromptFilters.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;

  let analysisId;
  let host;
  let token;
  beforeAll(function() {
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      if(analysisId){
        new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, analysisId);
      }
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['REPORTPROMPTFILTER']['reportPromptFilterDataProvider'], function(data, description) {
    it('should able to apply prompt filter for report: ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'REPORTPROMPTFILTER', dp:'reportPromptFilterDataProvider'}), () => {
      try {
        if(!token) {
          logger.error('token cannot be null');
          assert.isNotNull(token, 'token cannot be null');
        }

        let currentTime = new Date().getTime();
        let user = data.user;
        let name = Constants.REPORT + ' ' + globalVariables.e2eId + '-' + currentTime;
        let description = 'Description:' + Constants.REPORT + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;

        let analysisType = Constants.REPORT;
        let analysis = new AnalysisHelper().createNewAnalysis(host, token, name, description, analysisType, null);
        assert.isNotNull(analysis, 'analysis cannot be null');
        analysisId = analysis.contents.analyze[0].executionId.split('::')[0];
        let promptFilterFunctions = new PromptFilterFunctions();
        promptFilterFunctions.applyFilters(categoryName, subCategoryName, defaultCategory, user, name, description, analysisType, null, data.fieldName);
        //From analysis detail/view page
        promptFilterFunctions.verifyPromptFromDetailPage(categoryName, subCategoryName, defaultCategory, name, data, Constants.REPORT);
        //verifyPromptFromListView and by executing from action menu
        promptFilterFunctions.verifyPromptFromListView(categoryName, subCategoryName, defaultCategory, name, data, true);
        //verifyPromptFromCardView and by executing from action menu
        promptFilterFunctions.verifyPromptFromCardView(categoryName, subCategoryName, defaultCategory, name, data, true);
      } catch (e) {
        console.log(e);
      }

    });
  });
});
