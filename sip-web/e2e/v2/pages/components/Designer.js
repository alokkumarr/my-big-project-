'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SaveDialog = require('./SaveDialog');

class Designer extends SaveDialog {
  constructor() {
    super();
    this._designerButton = element(by.css(`[id="mat-button-toggle-1-button"]`));
    this._saveButton = element(by.css(`[e2e="designer-save-btn"]`));
    this._previewBtn = element(by.css(`[e2e="open-preview-modal"]`));
    this._filterBtn = element(by.css(`[e2e="open-filter-modal"]`));
    this._sortBtn = element(by.css(`[e2e="open-sort-modal"]`));
  }

  clickOnDesignerButton() {
    commonFunctions.clickOnElement(this._designerButton);
  }

  clickOnSave() {
    commonFunctions.clickOnElement(this._saveButton);
  }

  clickOnPreviewButton() {
    commonFunctions.clickOnElement(this._previewBtn);
  }
  clickOnFilterButton() {
    commonFunctions.waitFor.elementToBeClickable(this._saveButton);
    commonFunctions.clickOnElement(this._filterBtn);
  }

  clickOnSortButton() {
    commonFunctions.clickOnElement(this._sortBtn);
  }
}
module.exports = Designer;
