'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const UserManagementPage = require('../admin/UserManagementPage');

class RoleManagementPage extends UserManagementPage {
  constructor() {
    super()
    this._addRoleButton = element(by.cssContainingText('span', "ROLE"));
    this._roleName = element(by.css('[formcontrolname="roleName"]'));
    this._roleDescription = element(by.css('[formcontrolname="roleDesc"]'));
    /*this._statusDropdown = element(by.css('[formcontrolname="activeStatusInd"]'));
    this._activeStatusOption = element(by.css(`[class='mat-option ng-star-inserted mat-selected mat-active']`));
    this._inactiveStatusOption = element(by.css(`[class="mat-option ng-star-inserted"]`));*/
    this._roleTypeDropdown = element(by.css('[formcontrolname="roleType"]'));
    this._selectRoleType = roleType => element(by.cssContainingText('span', `${roleType}`));
    this._createRoleButton = element(by.css('[e2e="create-analysis-btn"]'));
    this._cancelRoleButton = element(by.cssContainingText('span', `Cancel`));
    this._toastMessage = element(by.xpath(`//*[@id="toast-container"]/div/div`));
    this._createdRole = role => element(by.cssContainingText('a',`${role}`));
    this._editRoleButton = value => element(by.xpath(`//a[text()='${value}']/following::mat-icon[@fonticon='icon-edit']`));
    this._deleteRoleButton = value => element(by.xpath(`//a[text()='${value}']/following::mat-icon[@fonticon='icon-trash']`));
    this._confirmDeleteButton = element(by.css(`[e2e='confirm-dialog-ok-btn']`));
  }

  clickAddRole() {
    logger.debug('Clicking on Add Role Button');
    commonFunctions.clickOnElement(this._addRoleButton);
  }

  clickRoleName() {
    commonFunctions.clickOnElement(this._roleName);
    browser.actions().sendKeys(protractor.Key.TAB).perform();
  }

  fillRoleName(roleName) {
    logger.debug('Filling Role Name with :' + roleName);
    commonFunctions.fillInput(this._roleName, roleName);
  }

  fillRoleDescription(roleDescription) {
    logger.debug('Filling Role Description with :' + roleDescription);
    commonFunctions.fillInput(this._roleDescription, roleDescription);
  }

  clickRoleType(){
    logger.debug('clicking on Role Type Dropdown ');
    commonFunctions.clickOnElement(this._roleTypeDropdown);
  }

  selectRoleType(roleType){
    logger.debug('Selecting Role Type is:' + roleType);
    commonFunctions.clickOnElement(this._selectRoleType(roleType));
  }

  clickCreateRole() {
    logger.debug('Clicking on Add Role Button');
    commonFunctions.clickOnElement(this._createRoleButton);
    }

  clickOnCancelButton() {
    logger.debug('Click on Cancel button');
    commonFunctions.clickOnElement(this._cancelRoleButton);
  }
  validateToastMessage(expectedmessage) {
    this._toastMessage().getText().then(actualMessage=>{
      if(actualMessage){
        expect(actualMessage).toBe(expectedmessage);
        logger.debug('Message Validation successful');
      }else {
        expect(false).toBe(true,'Message Validation is not successful');
      }
    })
  }

  selectCreatedRole(role) {
    commonFunctions.clickOnElement(this._createdRole(role));
  }

  editCreatedRole(role) {
    commonFunctions.clickOnElement(this._editRoleButton(role));
    browser.sleep(2000);
  }

  deleteRole(role) {
    commonFunctions.clickOnElement(this._deleteRoleButton(role));
  }

  clickConfirmDelete() {
  commonFunctions.waitFor.elementToBeVisible(this._confirmDeleteButton,5000);
    commonFunctions.clickOnElement(this._confirmDeleteButton);
  }

  validateRole(roleStatus) {
    this._roleName.getAttribute("aria-invalid").then(status =>{
      if(status){
        console.log("disabled status is "+status);
        console.log("role status is "+roleStatus);
        expect(status).toEqual(roleStatus);
        logger.debug('role status is matched and validation successful');
      }else {
        expect(false).toBe(
          true,
          'role Status validation is not successful'
        );
      }
    })
  }
}
module.exports = RoleManagementPage;
