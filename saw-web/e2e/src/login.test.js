var testDataReader = require('./testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const loginPage = require('./javascript/pages/loginPage.po.js');
const header = require('./javascript/pages/components/header.co.js');
const protractorConf = require('../protractor.conf');
const commonFunctions = require('./javascript/helpers/commonFunctions.js');
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

  using(testDataReader.testData['LOGIN']['loginDataProvider'], function (data, description) {
    it('Should successfully logged ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'LOGIN', dp:'loginDataProvider'}), function () {
      loginPage.loginAs(data.user);
      expect(header.headerElements.companyLogo.isPresent()).toBeTruthy();
    });
  });
});
