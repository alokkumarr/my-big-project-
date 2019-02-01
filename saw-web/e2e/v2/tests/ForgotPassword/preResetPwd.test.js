const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const preResetPwd = require('../../pages/preResetPwd');
const ForgotPasswordPage = require('../../pages/ForgotPasswordPage');
const PreResetHeader = require('../../pages/components/PreResetHeader');
const commonFunctions = require('../../pages/utils/commonFunctions')

describe('Executing reset password tests from preResetPwd.test.js', () => {

  beforeAll(() => {
    logger.info('Starting forgot password tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach((done) => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach((done) => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['PRERESETPASSWORD']['positiveTests'] ? testDataReader.testData['PRERESETPASSWORD']['positiveTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {
      commonFunctions.goToHome();
      const forgotPasswordPage = new ForgotPasswordPage();
      forgotPasswordPage.doClickOnForgotPassword();
      const prpwd = new preResetPwd();
      prpwd.resetAs(data.user,data.expected.message);
      const preResetHdr = new PreResetHeader();
      preResetHdr.verifyLogo();

    }).result.testInfo = { testId: id, data: data, feature: 'PRERESETPASSWORD', dataProvider: 'positiveTests' };
  });

  // using(testDataReader.testData['PRERESETPASSWORD']['negativeTests'] ? testDataReader.testData['PRERESETPASSWORD']['negativeTests'] : {}, (data, id) => {
  //   it(`${id}:${data.description}`, () => {
  //
  //     commonFunctions.goToHome();
  //     let PreResetPassword = new PreResetPassword();
  //     PreResetPassword.clickOnResetButton();
  //     PreResetPassword.verifyError(data.expected.message);
  //
  //   }).result.testInfo = { testId: id, data: data, feature: 'PRERESETPASSWORD', dataProvider: 'negativeTests' };
  // });

});
