'use strict';

const commonFunctions = require('../utils/commonFunctions');
const FilterDialog = require('./FilterDialog');

class SaveDialog extends FilterDialog {
  constructor() {
    super();
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
    commonFunctions.fillInput(this._analysisNameInput, name);
  }

  enterAnalysisDescription(description) {
    commonFunctions.fillInput(this._analysisDescriptionInput, description);
  }

  clickOnSaveAndCloseDialogButton(landingPageAfterSave = null) {
    commonFunctions.clickOnElement(this._saveAndCloseButton);
    commonFunctions.waitFor.elementToBeVisible(this._saveAndCloseButton);
    if (landingPageAfterSave) {
      commonFunctions.waitFor.pageToBeReady(landingPageAfterSave);
    } else {
      browser.sleep(2000);
    }
  }

  clickOnSaveDialogButton() {
    commonFunctions.clickOnElement(this._saveDialogButton);
    commonFunctions.waitFor.pageToBeReady(/edit/);
  }

  clickOnCancelDialogButton() {
    commonFunctions.clickOnElement(this._cancelDialogButton);
  }
}
module.exports = SaveDialog;