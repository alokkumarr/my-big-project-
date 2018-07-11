var appRoot = require('app-root-path');
const commonFunctions = require(appRoot + '/src/test/javascript/helpers/commonFunctions.js');
const users = require(appRoot + '/src/test/javascript/data/users.js');

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
    console.log('user---->'+user)
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

  /**
   * Login as a user from this list
   * https://confluence.synchronoss.net:8443/pages/viewpage.action?spaceKey=BDA&title=Users%2C+Roles+And+Privileges
   */
  loginAs(userName) {
    switch (userName) {
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
  }
};
