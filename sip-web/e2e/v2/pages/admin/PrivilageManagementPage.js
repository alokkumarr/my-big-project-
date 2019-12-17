'use-strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const UserManagementPage = require('../admin/UserManagementPage');

class PrivilageManagementPage  {
  constructor() {
    this._deletePrivilege = element(by.css(`[fonticon="icon-trash"]`));
    this._confirmDeleteButton = element(by.css(`[e2e='confirm-dialog-ok-btn']`));
  }

  deletePrivilege(role) {
    commonFunctions.clickOnElement(this._deletePrivilege);
  }

  clickConfirmDelete() {
    commonFunctions.waitFor.elementToBeVisible(this._confirmDeleteButton,5000);
    commonFunctions.clickOnElement(this._confirmDeleteButton);
  }
}
module.exports = PrivilageManagementPage;
