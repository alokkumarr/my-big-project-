const commonFunctions = require('../helpers/commonFunctions.js');
const users = require('../data/users.js');
const analyzePage = require('./analyzePage.po.js');

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
    console.log('user---->'+user);
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

  logOutLogin(user, password) {
    let _self = this;
    browser.ignoreSynchronization = false;
    analyzePage.goToHome();
    browser.ignoreSynchronization = true;
    commonFunctions.waitFor.pageToBeReady(/saw/);
    element(this.loginElements.userNameField.isPresent().then(function(isPresent) {
      if(isPresent) {
        console.log('Doing login...');
        _self.userLogin(user, password);
      } else {
        console.log('User is already logged in, doing logout...');
        analyzePage.main.logOut();
        commonFunctions.logOutByClearingLocalStorage();
        _self.userLogin(user, password);
      }
    }));

  },
  /**
   * Login as a user from this list
   * https://confluence.synchronoss.net:8443/pages/viewpage.action?spaceKey=BDA&title=Users%2C+Roles+And+Privileges
   */
  loginAs(userName) {
    switch (userName) {
      case 'admin':
        this.logOutLogin(users.admin.loginId, users.anyUser.password);
        break;
      case 'userOne':
        this.logOutLogin(users.userOne.loginId, users.anyUser.password);
        break;
      case 'user':
        this.logOutLogin('reportuser@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'analyst':
        this.logOutLogin('analyst@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      case 'reviewer':
        this.logOutLogin('reviewer@synchronoss.com', 'Sawsyncnewuser1!');
        break;
      default:
    }
  }
};
