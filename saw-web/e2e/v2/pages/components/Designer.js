'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SaveDialog = require('./SaveDialog');

class Designer extends SaveDialog {
  constructor() {
    super();
    this._designerButton = element(by.css(`[id="mat-button-toggle-1-button"]`));
    this._saveButton = element(by.css(`[e2e="designer-save-btn"]`));
    this._previewBtn = element(by.css(`[e2e="open-preview-modal"]`));
    this._filterBtn = element(by.xpath('//button/span[text()="Filter"]'));
    this._addFilter = tableName =>
      element(by.css(`[e2e="filter-add-btn-${tableName}"]`));
    this._filterColumnDropDown = element(
      by.css('input[e2e="filter-autocomplete-input"]')
    );
    this._columnNameDropDownItem = columnName =>
      element(
        by.xpath(`(//mat-option/span[contains(text(),"${columnName}")])[1]`)
      );
    // Date
    this._filterPresetDropDown = element(
      by.xpath('//span[contains(text(),"Custom")]')
    );
    this._presetDropDownItem = presetName =>
      element(by.xpath(`//mat-option[contains(text(),"${presetName}")]`));

    // Number
    this._filterNumberOperator = element(
      by.css('mat-select[e2e="filter-number-operator-select"]')
    );
    this._numberOperatorDropDownItem = operator =>
      element(
        by.css(`mat-option[e2e="filter-number-operator-option-${operator}"]`)
      );
    this._filterNumberInput = element(by.css('[e2e="designer-number-filter-input"]'));
    this._filterStringOperator = element(by.css('[e2e="filter-string-select"]'));
    this._stringOperatorDropDownItem = operator =>
      element(by.css(`mat-option[e2e="filter-string-option-${operator}"]`));
    this._filterStringInput = element(
      by.xpath(`(//input[contains(@id,"mat-input-")])[position()=last()]`)
    );
    this._filterStringIsInIsNotInInput = element(
      by.xpath(`//input[@e2e="designer-filter-string-input"]`)
    );
    this._applyFiltersBtn = element(
      by.xpath('//span[contains(text(),"Apply Filters")]')
    );
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

  clickOnAddFilterButtonByTableName(tableName) {
    commonFunctions.clickOnElement(this._addFilter(tableName));

  }

  clickOnColumnInput() {
      commonFunctions.clickOnElement(this._filterColumnDropDown);
  }

  clickOnColumnDropDown(name) {
    commonFunctions.clickOnElement(this._columnNameDropDownItem(name));
  }
  selectPreset(presetName) {
    commonFunctions.clickOnElement(this._filterPresetDropDown);
    commonFunctions.clickOnElement(this._presetDropDownItem(presetName));
  }

  selectNumberOperatorAndValue(operator, value) {
    commonFunctions.clickOnElement(this._filterNumberOperator);
    commonFunctions.clickOnElement(this._numberOperatorDropDownItem(operator));
    commonFunctions.fillInput(this._filterNumberInput, value);
  }

  selectStringOperatorAndValue(operator, value) {
    commonFunctions.clickOnElement(this._filterStringOperator);
    commonFunctions.clickOnElement(this._stringOperatorDropDownItem(operator));
    if (operator === 'Is in' || operator === 'Is not in') {
      commonFunctions.fillInput(this._filterStringIsInIsNotInInput, value);
    } else {
      commonFunctions.fillInput(this._filterStringInput, value);
    }
  }

  clickOnApplyFilterButton() {
    commonFunctions.clickOnElement(this._applyFiltersBtn);
  }
}
module.exports = Designer;
