'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const UserManagementPage = require('../admin/UserManagementPage');

class SideMenuNav  {
  constructor() {
    this._menuIcon = element(by.css('[e2e="main-menu-expand-btn"]'));
    this._userLink = element(by.css('[href="#/admin/user"]'));
    this._roleLink = element(by.css('[href="#/admin/role"]'));
    this._privilegeLink = element(by.css('[href="#/admin/privilege"]'));
    this._categoryLink = element(by.css('[href="#/admin/categories"]'));
    this._packageUtilityLink = element(by.cssContainingText('span', "Package Utility"));
    this._dataSecurityLink = element(by.cssContainingText('span', "Data Security"));
  }

  clickMenu() {
    logger.debug('Clicking on Menu Icon');
    commonFunctions.clickOnElement(this._menuIcon);
  }

  clickUser() {
    logger.debug('Clicking on Role Link');
    commonFunctions.clickOnElement(this._userLink);
  }

  clickRole() {
    logger.debug('Clicking on Role Link');
    commonFunctions.clickOnElement(this._roleLink);
  }

  clickPrivilege() {
    logger.debug('Clicking on privilege Link');
    commonFunctions.clickOnElement(this._privilegeLink);
  }

  clickCategory() {
    logger.debug('Clicking on category Link');
    commonFunctions.clickOnElement(this._categoryLink);
  }
}
module.exports = SideMenuNav;
