'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SaveDialog = require('./SaveDialog');

class Designer extends SaveDialog {
  constructor() {
    super();
    this._designerButton = element(by.css(`[id="mat-button-toggle-1-button"]`));
    this._saveButton = element(by.css(`[e2e="designer-save-btn"]`));
    this._previewBtn=element(by.css(`[e2e="open-preview-modal"]`))
  }

  clickOnDesignerButton() {
    commonFunctions.clickOnElement(this._designerButton);
  }

  clickOnSave() {
    commonFunctions.clickOnElement(this._saveButton);
  }

  clickOnPreviewButton(){
      commonFunctions.clickOnElement(this._previewBtn);
  }
}
module.exports = Designer;
