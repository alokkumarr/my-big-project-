'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const users = require('../../helpers/data-generation/users');
let Header = require('../components/Header');

class UserManagementPage {
  constructor() {
    // Initialize all elements
    this._loginErrorMessage = element(by.css('[e2e="error-msg"]'));
    this._addUser = element(by.xpath('//span[text()=\'USER\']'));
    this._roledropdown = element(by.css('[formcontrolname="roleId"]'));
    this._selectDropDownElement = Option => element(by.cssContainingText('span',`${Option}`));
    this._firstName = element(by.css('[formcontrolname="firstName"]'));
    this._middleName = element(by.css('[formcontrolname="middleName"]'));
    this._lastName = element(by.css('[formcontrolname="lastName"]'));
    this._loginId = element(by.css('[formcontrolname="masterLoginId"]'));
    this._password = element(by.css('[type="password"]'));
    this._emailId = element(by.css('[formcontrolname="email"]'));
    this._statusdropdown = element(by.css('[formcontrolname="activeStatusInd"]'));
    this._activeStatusOption = Option => element(by.xpath(`//span[text()=' ${Option} ']`));
    this._inactiveStatusOption = Option => element(by.cssContainingText('span',`${Option}`));
    this._createUser = element(by.css('[e2e="create-analysis-btn"]'));
    this._cancelButton = element(by.cssContainingText('span','Cancel'));
    this._toastMessage = message => element(by.cssContainingText('div',`${message}`));
    this._deleteUserName = value => element(by.xpath(`//div[text()='${value}']/following::mat-icon[@fonticon='icon-trash']`));
    this._confirmDeleteButton = element(by.css(`[e2e='confirm-dialog-ok-btn']`));
    this._passwordErrorAlert = element(by.cssContainingText('pre',`Password should have`));
  }

  clickNewUser() {
    logger.debug('Click on New User button');
    commonFunctions.waitFor.elementToBeVisible(this._addUser);
    this._addUser.click();
  }

  clickRole() {
    logger.debug('Click on role drop down button');
    this._roledropdown.click();
  }

  selectRole(selectiveOption) {
    logger.debug('selecting the roleType :' + selectiveOption);
    commonFunctions.clickOnElement(this._selectDropDownElement(selectiveOption));
  }


  validateStatusOfCreatedRole(createdRole) {
    commonFunctions.waitFor.elementToBeNotVisible(this._selectDropDownElement(createdRole));
    browser.actions().sendKeys(protractor.Key.ESCAPE).perform();
  }

  fillFirstName(firstName) {
    logger.debug('Filling firstName with :' + firstName);
    commonFunctions.fillInput(this._firstName, firstName);

  }

  fillMiddleName(middleName) {
    logger.debug('Filling password with :' + middleName);
    commonFunctions.fillInput(this._middleName, middleName);
  }

  fillLastName(lastName) {
    logger.debug('Filling password with :' + lastName);
    commonFunctions.fillInput(this._lastName, lastName);
  }

  fillLoginId(loginId) {
    logger.debug('Filling password with :' + loginId);
    commonFunctions.fillInput(this._loginId, loginId);
  }

  fillPassword(password) {
    logger.debug('Filling password with :' + password);
    commonFunctions.fillInput(this._password, password);
  }

  fillEmailId(emailid) {
    logger.debug('Filling password with :' + emailid);
    commonFunctions.fillInput(this._emailId, emailid);
  }

  clickStatus() {
    logger.debug('Click on role drop down button');
    commonFunctions.waitFor.elementToBeVisible(this._statusdropdown);
    this._statusdropdown.click();
  }

  selectStatus(status) {
    logger.debug('selecting the StatusType as active');
    commonFunctions.clickOnElement(this._activeStatusOption(status));
  }

  clickCancel() {
    logger.debug('Click on Cancel button');
    commonFunctions.clickOnElement(this._cancelButton);
  }

  clickCreate() {
    logger.debug('Click on Create button');
    commonFunctions.waitFor.elementToBeVisible(this._createUser);
    commonFunctions.clickOnElement(this._createUser);
  }

  /*Method to verify Toast message*/
  verifyToastMessage(message) {
    element(
      this._toastMessage(message).getText().then(value => {
        if (value) {
          expect(value.trim()).toEqual(message.trim());
          commonFunctions.clickOnElement(this._toastMessage(message));
        } else {
          expect(false).toBe(
            true,
            'Toast Message should be equal but mismatched'
          );
        }
      })
    );
  }

  validateEmail(emailStatus) {
    browser.actions().sendKeys(protractor.Key.TAB).perform();
    this._emailId.getAttribute("aria-invalid").then(status =>{
      if(status){
        console.log("disabled status is "+status);
        console.log("email status is "+emailStatus);
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

  DeleteCreatedUser(userName) {
    /*commonFunctions.waitFor.elementToBeVisible(this._deleteUserName(userName));
    commonFunctions.clickOnElement(this._deleteUserName(userName));*/
    this._deleteUserName(userName).isDisplayed().then(()=>{
      commonFunctions.clickOnElement(this._deleteUserName(userName));
      this.clickConfirmDelete();
    },()=>{
      console.log("User is not present to Delete" + userName);
    });
  }

  clickConfirmDelete() {
    commonFunctions.clickOnElement(this._confirmDeleteButton);
    }

  validateLoginErrorMessage(message) {
    this._loginErrorMessage.getText().then(errorMessage=>{
      if(errorMessage){
        expect(errorMessage).toBe(message);
        logger.debug('Login error message validation successful');
      }else {
        expect(false).toBe(true,'Login error message validation is not successful');
      }
    })
  }
  }


module.exports = UserManagementPage;
