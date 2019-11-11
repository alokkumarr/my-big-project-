'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const users = require('../../helpers/data-generation/users');
let Header = require('../components/Header');

class UserManagementPage {
  constructor() {
    // Initialize all elements
    
    this._addUser = element(by.css('[class="mat-raised-button mat-primary ng-star-inserted"]'));
    this._roledropdown=element(by.css('[formcontrolname="roleId"]'));
    this.listbox = 
    this._DropDownElement = selectiveOption =>
       element(
         by.xpath(
           `//span[@class="mat-option-text" and contains(text(), '${selectiveOption}')]`
         ));
    this._firstName = element(by.css('[placeholder="First name"]'));
    this._middleName = element(by.css('[placeholder="Middle name"]'));
    this._lastName = element(by.css('[placeholder="Last name"]'));
    this._loginId = element(by.css('[placeholder="Login Id"]'));
    this._password = element(by.css('[placeholder="Password"]'));
    this._emailId = element(by.css('[placeholder="Email"]'));
    this._statusdropdown=element(by.css('[formcontrolname="activeStatusInd"]'));
    this._createUser=element(by.css('[e2e="create-analysis-btn"]'));
    this._cancelButton=element(by.buttonText('Cancel'));

    
  }

  clickOnNewUserButton() {
    logger.debug('Click on New User button');
      commonFunctions.waitFor.elementToBeVisible(this._addUser);
      this._addUser.click();
    }

  clickOnRoleDropDownButton() {
      logger.debug('Click on role drop down button');
        commonFunctions.waitFor.elementToBeVisible(this._roledropdown);
        this._roledropdown.click();
      }

  selectDropDownOptions(selectiveOption) {
    logger.debug('selecting the roleType :' + selectiveOption);
    this._DropDownElement(selectiveOption).click();
    }

  fillFirstNameField(firstName) {
      logger.debug('Filling password with :' + firstName);
      commonFunctions.fillInput(this._firstName, firstName);
    }
  
  fillMiddleNameField(middleName) {
      logger.debug('Filling password with :' + middleName);
      commonFunctions.fillInput(this._middleName, middleName);
    }
  
  fillLastNameField(lastName) {
      logger.debug('Filling password with :' + lastName);
      commonFunctions.fillInput(this._lastName, lastName);
    }

  fillLoginIdField(loginId) {
      logger.debug('Filling password with :' + loginId);
      commonFunctions.fillInput(this._loginId, loginId);
    }
  
  fillPasswordField(password) {
      logger.debug('Filling password with :' + password);
      commonFunctions.fillInput(this._password, password);
    }

  fillEmailIdField(emailid) {
      logger.debug('Filling password with :' + emailid);
      commonFunctions.fillInput(this._emailId, emailid);
    }
  
  clickOnStatusDropDownButton() {
      logger.debug('Click on role drop down button');
        commonFunctions.waitFor.elementToBeVisible(this._statusdropdown);
        this._statusdropdown.click();
      }
  
  clickOnCancelButton() {
        logger.debug('Click on Cancel button');
          commonFunctions.waitFor.elementToBeVisible(this._cancelButton);
          this._cancelButton.click();
        }

  clickOnCreateButton() {
        logger.debug('Click on Create button');
          commonFunctions.waitFor.elementToBeVisible(this._createUser);
          this._createUser.click();
        }

}
module.exports = UserManagementPage;