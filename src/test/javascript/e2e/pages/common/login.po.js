module.exports = {
  loginElements: {
    userNameField: element(by.id('input_0')),
    passwordField: element(by.id('input_1')),
    loginBtn: element(by.buttonText('Login')),
    invalidErr: element(by.css('.err-msg'))
  },

  userLogin(user, password) {
    const userElem = this.loginElements.userNameField;
    const passwordElem = this.loginElements.passwordField;
    const loginElem = this.loginElements.loginBtn;

    userElem.clear().sendKeys(user);
    passwordElem.clear().sendKeys(password);
    loginElem.click();
  },

  /**
   * Login as a user from this list
   * https://confluence.synchronoss.net:8443/pages/viewpage.action?spaceKey=BDA&title=Users%2C+Roles+And+Privileges
   */
  loginAs(role) {
    const password = 'Sawsyncnewuser1!';
    switch (role) {
      case 'admin':
        this.userLogin('sawadmin@synchronoss.com', password);
        break;
      case 'user':
        this.userLogin('reportuser@synchronoss.com', password);
        break;
      case 'analyst':
        this.userLogin('analyst@synchronoss.com', password);
        break;
      case 'reviewer':
        this.userLogin('reviewer@synchronoss.com', password);
        break;
      default:
    }
  }
};
