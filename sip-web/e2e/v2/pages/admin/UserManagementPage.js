'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const users = require('../../helpers/data-generation/users');
let Header = require('../components/Header');

class UserManagementPage {
  constructor() {
    // Initialize all elements
    this._addUser = element(by.css('[e2e="admin-selector-user"]'));
    this._roledropdown = element(by.css('[e2e="role-types"]'));
    this._selectDropDownElement = Option => element(by.cssContainingText('span',`${Option}`));
    this._firstName = element(by.css('[e2e="first-name"]'));
    this._middleName = element(by.css('[e2e="middle-name"]'));
    this._lastName = element(by.css('[e2e="last-name"]'));
    this._loginId = element(by.css('[e2e="master-login-id"]'));
    this._password = element(by.css('[e2e="password-input"]'));
    this._emailId = element(by.css('[e2e="user-email"]'));
    this._statusdropdown = element(by.css('[e2e="user-status"]'));
    this._selectStatus = status => element(by.xpath(`//span[text()=' ${status} ']`));
    this._createUser = element(by.css('[e2e="create-analysis-btn"]'));
    this._cancelButton = element(by.cssContainingText('span','Cancel'));
    this._deleteUser = value => element(by.xpath(`//span[text()='${value}']/following::mat-icon[@fonticon='icon-trash']`));
    this._confirmDelete = element(by.css(`[e2e='confirm-dialog-ok-btn']`));
    this._passwordErrorAlert = element(by.cssContainingText('pre',`Password should have`));
    this._search = element(by.css(`[e2e="search-admin"]`));
    this._clickSearch =this._search.element(by.css(`[fonticon="icon-search"]`));
    this._searchUser = element(by.css(`[e2e="search-box-admin"]`));
    this._toastMessage = element(by.xpath(`//div[@class='toast-message']`));
  }

  addUser() {
    commonFunctions.waitFor.elementToBeVisible(this._addUser);
    commonFunctions.clickOnElement(this._addUser);
  }

  clickRole() {
    commonFunctions.clickOnElement(this._roledropdown);
  }

  chooseRole(Option) {
    commonFunctions.clickOnElement(this._selectDropDownElement(Option));
  }

  fillFirstName(firstName) {
    commonFunctions.fillInput(this._firstName, firstName);
  }

  fillMiddleName(middleName) {
    commonFunctions.fillInput(this._middleName, middleName);
  }

  fillLastName(lastName) {
    commonFunctions.fillInput(this._lastName, lastName);
  }

  fillLoginId(loginId) {
    commonFunctions.fillInput(this._loginId, loginId);
  }

  fillPassword(password) {
    commonFunctions.fillInput(this._password, password);
  }

  fillEmail(emailid) {
    commonFunctions.fillInput(this._emailId, emailid);
  }

  clickStatus() {
    commonFunctions.waitFor.elementToBeVisible(this._statusdropdown);
    commonFunctions.clickOnElement(this._statusdropdown);
  }

  selectStatus(status) {
    commonFunctions.clickOnElement(this._selectStatus(status));
  }

  clickCancel() {
    commonFunctions.clickOnElement(this._cancelButton);
  }

  createUser() {
    commonFunctions.waitFor.elementToBeVisible(this._createUser);
    commonFunctions.clickOnElement(this._createUser);
  }

  searchUser(userName) {
    commonFunctions.clickOnElement(this._clickSearch);
    commonFunctions.fillInput(this._searchUser,userName);
  }

  deleteUser(userName) {
    commonFunctions.clickOnElement(this._deleteUser(userName));
    commonFunctions.clickOnElement(this._confirmDelete);
  }

  generateRandomString(dataType,length) {
      var result = '';
      var characters = dataType;
      var charactersLength = characters.length;
      for (var i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
      }
      return result;
    }

  validateEmail(emailStatus) {
    browser.actions().sendKeys(protractor.Key.TAB).perform();
    this._emailId.getAttribute("aria-invalid").then(status =>{
      if(status){
        expect(status).toEqual(emailStatus);
        logger.debug('Email status is matched and validation successful');
      }else {
        expect(false).toBe(
          true,
          'Email Status validation is not successful'
        );
      }
    })
  }

  validatePassword(passWordStatus) {
    this._password.getAttribute("aria-invalid").then(status =>{
      if(status)
      {
        expect(status).toEqual(passWordStatus);
        logger.debug('PassWord status is matched and validation successful');
      }else {
        expect(false).toBe(
          true,
          'PassWord Status validation is not successful'
        );
      }
    });
  }

  validatePasswordErrorText(ErrorMessage) {
    this._passwordErrorAlert.getText().then( passwordError=> {
      if(passwordError){
        expect(passwordError).toContain(ErrorMessage);
        logger.debug('PassWord Error Message validation successful');
      }else {
        expect(false).toBe(
          true,
          'PassWord Error Message validation is not successful'
        );
      }
    })
  }
}

module.exports = UserManagementPage;
