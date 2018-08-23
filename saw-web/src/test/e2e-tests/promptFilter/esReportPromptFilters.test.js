const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const protractorConf = require('../../../../conf/protractor.conf');
const using = require('jasmine-data-provider');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
let AnalysisHelper = require('../../javascript/api/AnalysisHelper');
let ApiUtils = require('../../javascript/api/APiUtils');
const Constants = require('../../javascript/api/Constants');
const globalVariables = require('../../javascript/helpers/globalVariables');
const PromptFilterFunctions = require('../../javascript/helpers/PromptFilterFunctions');

describe('Prompt filter tests: esReportPromptFilters.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;

  let analysisId;
  let host;
  let token;
  //Note: Commented test for other user because it took 1hr 6 min to execute all tests
  const chartDataProvider = {
    // DATES
    'Date data type filter as admin': {
      user: 'admin',
      fieldType: 'date',
      value: 'This Week',
      fieldName: 'Date'
    },
    'Date data type filter': {
      user: 'userOne',
      fieldType: 'date',
      value: 'This Week',
      fieldName: 'Date'
    },
    //NUMBERS
    'Number data type filter as admin': {
      user: 'admin',
      fieldType: 'number',
      operator: 'Equal to',
      value: 99999.33,
      fieldName: 'Double'
    },
    'Number data type filter': {
      user: 'userOne',
      fieldType: 'number',
      operator: 'Equal to',
      value: 99999.33,
      fieldName: 'Double'
    },
    //STRING
    'String data type filter as admin': {
      user: 'admin',
      fieldType: 'string',
      operator: 'Equals',
      value: 'string 450',
      fieldName: 'String'
    },
    'String data type filter': {
      user: 'userOne',
      fieldType: 'string',
      operator: 'Equals',
      value: 'string 450',
      fieldName: 'String'
    }
  };

  beforeAll(function() {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function(done) {
    setTimeout(function() {
      //expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, analysisId);
      //analyzePage.main.doAccountAction('logout');
      //commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });
  afterAll(function() {
    commonFunctions.logOutByClearingLocalStorage();
  });

  using(chartDataProvider, function(data, description) {
    it('should able to apply prompt filter for esReport: ' + description, () => {
      try {
        let currentTime = new Date().getTime();
        let user = data.user;
        let name = Constants.ES_REPORT + ' ' + globalVariables.e2eId + '-' + currentTime;
        let description = 'Description:' + Constants.ES_REPORT + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;
        let analysisType = Constants.ES_REPORT;
        let analysis = new AnalysisHelper().createNewAnalysis(host, token, name, description, analysisType, null);
        analysisId = analysis.contents.analyze[0].executionId.split('::')[0];
        let promptFilterFunctions = new PromptFilterFunctions();
        promptFilterFunctions.applyFilters(categoryName, subCategoryName, defaultCategory, user, name, description, analysisType, null, data.fieldName);
        //From analysis detail/view page
        promptFilterFunctions.verifyPromptFromDetailPage(categoryName, subCategoryName, defaultCategory, name, data);
        //verifyPromptFromListView and by executing from action menu
        promptFilterFunctions.verifyPromptFromListView(categoryName, subCategoryName, defaultCategory, name, data, true);
        //verifyPromptFromListView and by clicking on analysis
        promptFilterFunctions.verifyPromptFromListView(categoryName, subCategoryName, defaultCategory, name, data, false);
        //verifyPromptFromCardView and by executing from action menu
        promptFilterFunctions.verifyPromptFromCardView(categoryName, subCategoryName, defaultCategory, name, data, true);
        //verifyPromptFromCardView and by clicking on analysis
        promptFilterFunctions.verifyPromptFromCardView(categoryName, subCategoryName, defaultCategory, name, data, false);
      } catch (e) {
        console.log(e);
      }
    });
  });
});
