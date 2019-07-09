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
    this._dataOptionsTab = element(
      by.xpath(`//*[contains(text(),'DATA OPTIONS')]`)
    );
    this._chartOptionsTab = element(
      by.xpath(`//*[contains(text(),' CHART OPTIONS')]`)
    );
    this._fieldsSection = name =>
      element(by.css(`[e2e=designer-data-option-${name}]`));
    this._topNBtn = element(by.css(`[e2e='top-btn']`));
    this._bottomNBtn = element(by.css(`[e2e='bottom-btn']`));
    this._limitValue = element(by.css(`[e2e='limit-value']`));
    this._aliasInput = field => element(by.css(`[e2e='alias-input-${field}]`));
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
    commonFunctions.clickOnElement(this._filterBtn);
  }

  clickOnSortButton() {
    commonFunctions.clickOnElement(this._sortBtn);
  }

  clickOnDataOptionsTab() {
    commonFunctions.clickOnElement(this._dataOptionsTab);
  }

  clickOnChartOptionsTab() {
    commonFunctions.clickOnElement(this._chartOptionsTab);
  }
  clickOnFieldsByName(name) {
    commonFunctions.clickOnElement(this._fieldsSection(name));
  }
  clickOnTopNButton() {
    commonFunctions.clickOnElement(this._topNBtn);
  }
  clickOnBottomNButton() {
    commonFunctions.clickOnElement(this._bottomNBtn);
  }
  fillLimitValue(value) {
    commonFunctions.fillInput(this._limitValue, value);
  }

  fillAliasInput(value) {
    commonFunctions.fillInput(this._aliasInput(), value);
  }
}
module.exports = Designer;
