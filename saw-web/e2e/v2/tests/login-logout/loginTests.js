var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const Header = require('../../pages/components/Header');
const commonFunctions = require('../../pages/utils/commonFunctions')

describe('Executing login tests from loginTests.js', () => {

  beforeAll(function () {
    logger.info('Starting login tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      commonFunctions.goToHome();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['LOGIN']['positiveTests'] ? testDataReader.testData['LOGIN']['positiveTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {

      let loginPage = new LoginPage();
      loginPage.loginAs(data.user, /analyze/);
      let header = new Header();
      header.verifyLogo();

    }).result.testInfo = { testId: id, data: data, feature: 'LOGIN', dataProvider: 'positiveTests' };
  });

  using(testDataReader.testData['LOGIN']['negativeTests'] ? testDataReader.testData['LOGIN']['negativeTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {

      let loginPage = new LoginPage();
      loginPage.fillUserNameField(data.user);
      loginPage.fillPasswordField(data.password);
      loginPage.clickOnLoginButton();
      loginPage.verifyError(data.expected.message);

    }).result.testInfo = { testId: id, data: data, feature: 'LOGIN', dataProvider: 'negativeTests' };
  });

});
