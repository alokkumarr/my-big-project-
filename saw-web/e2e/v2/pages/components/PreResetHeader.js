'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions')
const preResetPwd = require('../../pages/preResetPwd');

class PreResetHeader {
  constructor() {
    this._companyLogo = element(by.css(`[e2e="brand-logo"]`));

  }

  clickLogin() {
    logger.silly('doing login..');
    new preResetPwd().clickOnLoginButton();
    commonFunctions.waitFor.pageToBeReady(/login/);
    commonFunctions.clearLocalStorage();
  }

  verifyLogo() {
    expect(this._companyLogo.isPresent()).toBeTruthy();
  }
}
module.exports = PreResetHeader;
