'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const SideNav = require('./SideNav');

class Header extends SideNav {
  constructor() {
    super();
    this._accountSettingIcon = element(by.css(`[e2e='account-settings-menu-btn']`));
    this._accountChangePasswordLink = element(by.css(`[e2e='account-settings-selector-change-password']`));
    this._accountLogoutLink = element(by.css(`[e2e='account-settings-selector-logout']`));

    this._companyLogo = element(by.css('.company-logo'));
    this._categoryMenuIcon = element(by.css(`[e2e="main-menu-expand-btn"]`));

    this._launcherButton = element(by.css('[class="header__module-launcher-button"]'));
    this._observeLink = element(by.xpath('//a[contains(@class,"module-observe")]'));
    this._analyzeLink = element(by.xpath('//a[contains(@class,"module-analyze")]'));
    this._progressBar = element(by.css('mat-progress-bar[mode="indeterminate"]'));
    this._workbenchLink = element(by.xpath('//a[contains(@class,"module-workbench")]'));
    this._toastMessage = element(by.css(`[id="toast-container"]`));
    this._adminLink = element(
            by.xpath('//a[contains(@class,"module-admin")]')
    );
  }

  clickOnModuleLauncher() {
    commonFunctions.clickOnElement(this._launcherButton);
  }

  clickOnObserveLink() {
    commonFunctions.clickOnElement(this._observeLink);
  }

  doChangePassword() {
    logger.silly('Clicking on ChangePassword button..');
    commonFunctions.waitFor.elementToBeVisible(this._accountSettingIcon);
    if (!this._accountSettingIcon.isPresent()) {
      commonFunctions.goToHome();
    }
    commonFunctions.clickOnElement(this._accountSettingIcon);
    commonFunctions.clickOnElement(this._accountChangePasswordLink);
    commonFunctions.waitFor.pageToBeReady(/changePwd/);
  }

  verifyChangePassword() {
    commonFunctions.waitFor.pageToBeReady(/login/);
  }

  doLogout() {
    logger.silly('doing logout..');
    if (!this._accountSettingIcon.isPresent()) {
      commonFunctions.goToHome();
    }
    commonFunctions.clickOnElement(this._accountSettingIcon);
    commonFunctions.clickOnElement(this._accountLogoutLink);
    commonFunctions.waitFor.pageToBeReady(/login/);
    commonFunctions.clearLocalStorage();
  }

  verifyLogo() {
    commonFunctions.waitFor.elementToBeVisible(this._companyLogo);
    expect(this._companyLogo.isPresent()).toBeTruthy();
  }
  openCategoryMenu() {
    commonFunctions.clickOnElement(this._categoryMenuIcon);
  }

  hideProgressBar() {
    commonFunctions.waitFor.elementToBeNotVisible(this._progressBar);
  }

  clickOnLauncher() {
    commonFunctions.clickOnElement(this._launcherButton);
  }

  clickOnWorkBench() {
    commonFunctions.clickOnElement(this._workbenchLink);
  }

  clickOnToastMessage() {
    commonFunctions.clickOnElement(this._toastMessage);
  }
  clickOnAdminLink(PageName) {
    commonFunctions.clickOnElement(this._adminLink);
  }
  verifyStatusOfAdminLink() {
    commonFunctions.waitFor.elementToBeNotVisible(this._adminLink);
  }
}
module.exports = Header;
