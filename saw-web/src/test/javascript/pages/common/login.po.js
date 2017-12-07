const commonFunctions = require('../../helpers/commonFunctions.js');
const users = require('../../data/users.js');

module.exports = {
  loginElements: {
    userNameField: element(by.id('input_0')),
    passwordField: element(by.id('input_1')),
    loginBtn: element(by.buttonText('Login')),
    invalidErr: element(by.css('.err-msg'))
  },

  // Wait after login is provided to prevent elements manipulation when page is not ready yet
  userLogin(user, password) {
    const userElem = this.loginElements.userNameField;
    const passwordElem = this.loginElements.passwordField;
    const loginElem = this.loginElements.loginBtn;

    userElem.clear().sendKeys(user);
    passwordElem.clear().sendKeys(password);
    loginElem.click();
    return commonFunctions.waitFor.pageToBeReady(/analyze/);
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
  }
};
