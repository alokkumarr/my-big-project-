'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
class SaveDialog {
  constructor() {
    this._analysisNameInput = element(by.css(`[e2e="save-dialog-name"]`));
    this._analysisDescriptionInput = element(
      by.css(`[e2e="save-dialog-description"]`)
    );
    this._saveAndCloseButton = element(
      by.css(`[e2e="save-dialog-save-analysis"]`)
    );
    this._saveDialogButton = element(by.css(`[e2e="dialog-save-analysis"]`));
    this._cancelDialogButton = element(
      by.css(`[e2e="designer-dialog-cancel"]`)
    );
  }

  enterAnalysisName(name) {
    commonFunctions.waitFor.elementToBeVisible(this._analysisNameInput);
    this._analysisNameInput.clear().sendKeys(name);
  }

  enterAnalysisDescription(description) {
    commonFunctions.waitFor.elementToBeVisible(this._analysisDescriptionInput);
    this._analysisDescriptionInput.clear().sendKeys(description);
  }

  clickOnSaveAndCloseDialogButton(landingPageAfterSave = null) {
    commonFunctions.waitFor.elementToBeClickable(this._saveAndCloseButton);
    this._saveAndCloseButton.click();
    if (landingPageAfterSave) {
      commonFunctions.waitFor.pageToBeReady(landingPageAfterSave);
    }
  }

  clickOnSaveDialogButton() {
    commonFunctions.waitFor.elementToBeClickable(this._saveDialogButton);
    this._saveDialogButton.click();
  }

  clickOnCancelDialogButton() {
    commonFunctions.waitFor.elementToBeClickable(this._cancelDialogButton);
    this._cancelDialogButton.click();
  }
}
module.exports = SaveDialog;
