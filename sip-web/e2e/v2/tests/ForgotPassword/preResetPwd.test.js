const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const PreResetPwd = require('../../pages/PreResetPwd');
const ForgotPasswordPage = require('../../pages/ForgotPasswordPage');
const PreResetHeader = require('../../pages/components/PreResetHeader');
const commonFunctions = require('../../pages/utils/commonFunctions');

describe('Executing reset password tests from preResetPwd.test.js', () => {
  beforeAll(() => {
    logger.info('Starting forgot password tests...');
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
    testDataReader.testData['PRERESETPASSWORD']['positiveTests']
      ? testDataReader.testData['PRERESETPASSWORD']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        commonFunctions.goToHome();
        new ForgotPasswordPage().doClickOnForgotPassword();
        new PreResetPwd().resetAs(data.user, data.expected.message);
        new PreResetHeader().verifyLogo();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PRERESETPASSWORD',
        dataProvider: 'positiveTests'
      };
    }
  );

  using(
    testDataReader.testData['PRERESETPASSWORD']['negativeTests']
      ? testDataReader.testData['PRERESETPASSWORD']['negativeTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        commonFunctions.goToHome();
        new ForgotPasswordPage().doClickOnForgotPassword();
        new PreResetPwd().resetAs(data.user, data.expected.message);
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PRERESETPASSWORD',
        dataProvider: 'negativeTests'
      };
    }
  );
});
