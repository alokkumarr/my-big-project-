import {it} from "@angular/core/testing/src/testing_internal";

var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const ResetPassword = require('../../pages/ForgotPasswordPage');
const commonFunctions = require('../../pages/utils/commonFunctions')

describe('Executing forgot password tests from forgotpassword.test.js', () => {

  beforeAll(() => {
    logger.info('Starting forgot password tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

    it( () => {

      commonFunctions.goToHome();
      let ForgotPasswordPage = new ForgotPasswordPage();
      ForgotPasswordPage.doClickOnForgotPassword();
    });
  });


