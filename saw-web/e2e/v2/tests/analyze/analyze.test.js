const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const commonFunctions = require('../../pages/utils/commonFunctions');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');

describe('Executing analyze tests from analyze.test.js', () => {
  beforeAll(() => {
    logger.info('Starting analyze tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['ANALYZE']['listView']
      ? testDataReader.testData['ANALYZE']['listView']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        //Start the test here
        new LoginPage().loginAs(data.user);
        const analyzePage = new AnalyzePage();
        analyzePage.goToView('list');
        analyzePage.verifyLabels(data.labels);
        analyzePage.verifyElementPresent(
          analyzePage._addAnalysisButton,
          true,
          'add analysis button should be displayed'
        );
        analyzePage.clickOnAnalysisTypeSelector();
        analyzePage.verifyAnalysisTypeOptions(data.options);

        //End the test here
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'ANALYZE',
        dataProvider: 'listView'
      };
    }
  );
});
