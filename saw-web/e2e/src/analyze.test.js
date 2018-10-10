var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const users = require('../javascript/data/users.js');
const ec = protractor.ExpectedConditions;
const protractorConf = require('../protractor.conf');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');

describe('Verify basic functionality on Analyze page: analyze.test.js', () => {

  //Prerequisites: two users should exist with user types: admin and user
  const userDataProvider = {
    'admin': {user: users.admin.loginId, role:'admin'}, // SAWQA-76
    'user': {user: users.userOne.loginId, role:'userOne'}, // SAWQA-4833
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['ANALYZE']['analyzeDataProvider'] ? testDataReader.testData['ANALYZE']['analyzeDataProvider']: {}, function (data, description) {
      it('should display list view by default '+description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'ANALYZE', dp:'analyzeDataProvider'}), function () {
        loginPage.loginAs(data.user);
        analyzePage.validateListView();
      });
  });

  using(testDataReader.testData['ANALYZE']['analyzeDataProvider2'] ? testDataReader.testData['ANALYZE']['analyzeDataProvider2'] : {}, function (data, description) {
    it('should land on analyze page ' +description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'ANALYZE', dp:'analyzeDataProvider2'}), function () {
      loginPage.loginAs(data.user);
      // the app should automatically navigate to the analyze page
      // and when its on there the current module link is disabled
      const alreadyOnAnalyzePage = ec.urlContains('/analyze');

      // wait for the app to automatically navigate to the default page
      browser
        .wait(() => alreadyOnAnalyzePage, protractorConf.timeouts.pageResolveTimeout)
        .then(() => expect(browser.getCurrentUrl()).toContain('/analyze'));
    });
  });
});
