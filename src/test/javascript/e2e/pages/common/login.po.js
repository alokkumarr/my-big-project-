module.exports = {
  loginElements: {
    userNameField: element(by.id('input_0')),
    passwordField: element(by.id('input_1')),
    loginBtn: element(by.css('.e2e-login-btn')),
    invalidErr: element(by.css('.err-msg'))
  },

  userLogin(user, password) {
    const userElem = this.loginElements.userNameField;
    const passwordElem = this.loginElements.passwordField;
    const loginElem = this.loginElements.loginBtn;

    userElem.clear().sendKeys(user);
    passwordElem.clear().sendKeys(password);
    loginElem.click();
  }
};
