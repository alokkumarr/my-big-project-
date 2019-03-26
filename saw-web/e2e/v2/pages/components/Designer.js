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
    this._columnDropDown = element(
      by.css('input[e2e="filter-autocomplete-input"]')
    );
    this._columnNameDropDownItem = columnName =>
      element(
        by.xpath(`(//mat-option/span[contains(text(),"${columnName}")])[1]`)
      );
    // Date
    this._presetDropDown = element(
      by.xpath('//span[contains(text(),"Custom")]')
    );
    this._presetDropDownItem = presetName =>
      element(by.xpath(`//mat-option[contains(text(),"${presetName}")]`));

    // Number
    this._numberOperator = element(
      by.css('mat-select[e2e="filter-number-operator-select"]')
    );
    (this._numberOperatorDropDownItem = operator =>
      element(
        by.css(`mat-option[e2e="filter-number-operator-option-${operator}"]`)
      )),
      (this._numberInput = element(by.xpath('//input[@type="number"]')));
    (this._stringOperator = element(by.css('[e2e="filter-string-select"]'))),
      (this._stringOperatorDropDownItem = operator =>
        element(by.css(`mat-option[e2e="filter-string-option-${operator}"]`))),
      (this._stringInput = element(
        by.xpath(`(//input[contains(@id,"mat-input-")])[position()=last()]`)
      )),
      (this._stringIsInIsNotInInput = element(
        by.xpath(`//input[@e2e="designer-filter-string-input"]`)
      ));
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
    commonFunctions.clickOnElement(this._columnDropDown);
  }

  clickOnColumnDropDown(name) {
    commonFunctions.clickOnElement(this._columnNameDropDownItem(name));
  }
  selectPreset(presetName) {
    commonFunctions.clickOnElement(this._presetDropDown);
    commonFunctions.clickOnElement(this._presetDropDownItem(presetName));
  }

  selectNumberOperatorAndValue(operator, value) {
    commonFunctions.clickOnElement(this._numberOperator);
    commonFunctions.clickOnElement(this._numberOperatorDropDownItem(operator));
    commonFunctions.fillInput(this._numberInput, value);
  }

  selectStringOperatorAndValue(operator, value) {
    commonFunctions.clickOnElement(this._stringOperator);
    commonFunctions.clickOnElement(this._stringOperatorDropDownItem(operator));
    if (operator === 'Is in' || operator === 'Is not in') {
      commonFunctions.fillInput(this._stringIsInIsNotInInput, value);
    } else {
      commonFunctions.fillInput(this._stringInput, value);
    }
  }
}
module.exports = Designer;
