var testDataReader = require('../e2e-tests/testdata/testDataReader.js');
const loginPage = require('../javascript/pages/loginPage.po.js');
const header = require('../javascript/pages/components/header.co.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const users = require('../javascript/data/users.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../conf/protractor.conf');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
var fs = require('fs');

describe('Login Tests: login.test.js', () => {

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

  afterAll(function () {
  });

  using(testDataReader.testData['LOGIN']['loginDataProvider'], function (data, description) {
    it('Should successfully logged in by ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'LOGIN', dp:'loginDataProvider'}), function () {
      loginPage.loginAs(data.role);
      expect(header.headerElements.companyLogo.isPresent()).toBeTruthy();
    });
  });
});
