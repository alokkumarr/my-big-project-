const commonFunctions = require('../helpers/commonFunctions.js');
const utils = require('../helpers/utils.js');
const users = require('../data/users.js');
const analyzePage = require('../pages/analyzePage.po.js');
const protractorConf = require('../../../../conf/protractor.conf');

module.exports = {
  loginElements: {
    userNameField: element(by.id('mat-input-0')),
    passwordField: element(by.id('mat-input-1')),
    loginBtn: element(by.buttonText('Login')),
    invalidErr: element(by.css('.err-msg')),
    parentElem: element(by.css('header > mat-toolbar')),
    btnSelector: 'mat-icon[e2e="account-settings-menu-btn"]',
    optionSelector: `button[e2e="account-settings-selector-logout"]`
  },

  // Wait after login is provided to prevent elements manipulation when page is not ready yet
  userLogin(user, password) {
    const userElem = this.loginElements.userNameField;
    const passwordElem = this.loginElements.passwordField;
    const loginElem = this.loginElements.loginBtn;
    commonFunctions.waitFor.elementToBeVisible(userElem);
    userElem.clear().sendKeys(user);
    commonFunctions.waitFor.elementToBeVisible(passwordElem);
    passwordElem.clear().sendKeys(password);
    commonFunctions.waitFor.elementToBeClickable(loginElem);
    loginElem.click();
    return commonFunctions.waitFor.pageToBeReady(/analyze/);
  },
  userLoginV2(user, password) {
    let self = this;
    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        let pattern = new RegExp("/login");
        let res = pattern.test(url);
        if (res) {
          expect(browser.getCurrentUrl()).toContain('/login');
          return self.userLogin(user, password);
        } else {
          commonFunctions.openBaseUrl();
          utils.doMdSelectOption({
            parentElem: element(by.css('header > mat-toolbar')),
            btnSelector: 'mat-icon[e2e="account-settings-menu-btn"]',
            optionSelector: `button[e2e="account-settings-selector-logout"]`
          });
          browser.waitForAngular();
          expect(browser.getCurrentUrl()).toContain('/login');
          return self.userLogin(user, password);
        }
      });
    }, protractorConf.timeouts.fluentWait);
  },

  /**
   * Login as a user from this list
   * https://confluence.synchronoss.net:8443/pages/viewpage.action?spaceKey=BDA&title=Users%2C+Roles+And+Privileges
   */
  loginAs(roleName) {
    switch (roleName) {
      case 'admin':
        this.userLogin(users.admin.loginId, users.anyUser.password);
        break;
      case 'userOne':
        this.userLogin(users.userOne.loginId, users.anyUser.password);
        break;
      case 'user':
        this.userLogin('reportuser@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'analyst':
        this.userLogin('analyst@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'reviewer':
        this.userLogin('reviewer@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      default:
    }
  },
  loginAsV2(roleName) {
    switch (roleName) {
      case 'admin':
        this.userLoginV2(users.admin.loginId, users.anyUser.password);
        break;
      case 'userOne':
        this.userLoginV2(users.userOne.loginId, users.anyUser.password);
        break;
      case 'user':
        this.userLoginV2('reportuser@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'analyst':
        this.userLoginV2('analyst@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'reviewer':
        this.userLoginV2('reviewer@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      default:
    }
  }
};
