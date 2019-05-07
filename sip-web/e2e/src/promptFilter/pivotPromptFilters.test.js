var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
let AnalysisHelper = require('../../v2/helpers/api/AnalysisHelper');
const Constants = require('../javascript/api/Constants');
const globalVariables = require('../javascript/helpers/globalVariables');
const PromptFilterFunctions = require('../javascript/helpers/PromptFilterFunctions');
const chai = require('chai');
const assert = chai.assert;
const logger = require('../../v2/conf/logger')(__filename);
let APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');

describe('pivot Prompt filter tests: pivotPromptFilters.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;

  let analysisId;
  let host;
  let token;

  beforeAll(function() {
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          analysisId
        );
      }
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PIVOTPROMPTFILTER'][
      'pivotPromptFilterDataProvider'
    ],
    function(data, description) {
      it(
        'should able to apply prompt filter for pivot: ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'PIVOTPROMPTFILTER',
            dp: 'pivotPromptFilterDataProvider'
          }),
        () => {
          try {
            if (!token) {
              logger.error('token cannot be null');
              expect(token).toBeTruthy();
              assert.isNotNull(token, 'token cannot be null');
            }

            let currentTime = new Date().getTime();
            let user = data.user;
            let name =
              Constants.PIVOT + ' ' + globalVariables.e2eId + '-' + currentTime;
            let description =
              'Description:' +
              Constants.PIVOT +
              ' for e2e ' +
              globalVariables.e2eId +
              '-' +
              currentTime;

            let analysisType = Constants.PIVOT;
            let analysis = new AnalysisHelper().createNewAnalysis(
              host,
              token,
              name,
              description,
              analysisType,
              null
            );
            expect(analysis).toBeTruthy();
            assert.isNotNull(analysis, 'analysis cannot be null');
            analysisId = analysis.contents.analyze[0].executionId.split(
              '::'
            )[0];
            let promptFilterFunctions = new PromptFilterFunctions();
            promptFilterFunctions.applyFilters(
              categoryName,
              subCategoryName,
              defaultCategory,
              user,
              name,
              description,
              analysisType,
              null,
              data.fieldName
            );
            //From analysis detail/view page
            promptFilterFunctions.verifyPromptFromDetailPage(
              'My Analysis',
              'DRAFTS',
              defaultCategory,
              name,
              data
            );
            //verifyPromptFromListView and by executing from action menu
            promptFilterFunctions.verifyPromptFromListView(
              'My Analysis',
              'DRAFTS',
              defaultCategory,
              name,
              data,
              true
            );
            //verifyPromptFromListView and by clicking on analysis
            promptFilterFunctions.verifyPromptFromListView(
              'My Analysis',
              'DRAFTS',
              defaultCategory,
              name,
              data,
              false
            );
            //verifyPromptFromCardView and by executing from action menu
            promptFilterFunctions.verifyPromptFromCardView(
              'My Analysis',
              'DRAFTS',
              defaultCategory,
              name,
              data,
              true
            );
            //verifyPromptFromCardView and by clicking on analysis
            promptFilterFunctions.verifyPromptFromCardView(
              'My Analysis',
              'DRAFTS',
              defaultCategory,
              name,
              data,
              false
            );
          } catch (e) {
            console.log(e);
          }
        }
      );
    }
  );
});
