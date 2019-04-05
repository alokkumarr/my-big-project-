'use strict';

const commonFunctions = require('../utils/commonFunctions');

class FilterDialog {
  constructor() {
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
    this._filterNumberInput = element(
      by.css('[e2e="designer-number-filter-input"]')
    );
    this._filterStringOperator = element(
      by.css('[e2e="filter-string-select"]')
    );
    this._stringOperatorDropDownItem = operator =>
      element(by.css(`mat-option[e2e="filter-string-option-${operator}"]`));
    this._filterStringInput = element(
      by.css(`[e2e="e2e-filter-string-input-other"]`)
    );
    this._filterStringIsInIsNotInInput = element(
      by.xpath(`//input[@e2e="designer-filter-string-input"]`)
    );
    this._applyFiltersBtn = element(
      by.xpath('//span[contains(text(),"Apply Filters")]')
    );
    this._promptCheckBox = element(
      by.xpath(`//span[contains(text(),'Prompt')]/parent::*`)
    );
    this._filterDialogText = element(by.xpath(`//strong[text()='Filter']`));
    this._cancleFilterPromptBtn = element(
      by.css(`button[e2e="designer-dialog-cancel"]`)
    );
    this._selectedFilterField = element(
      by.css(`[e2e="filter-autocomplete-input"]`)
    );
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

  clickOnPromptCheckBox() {
    commonFunctions.clickOnElement(this._promptCheckBox);
  }

  shouldFilterDialogPresent() {
    commonFunctions.waitFor.elementToBeVisible(this._filterDialogText);
  }

  clickOnCancelFilterModelButton() {
    commonFunctions.clickOnElement(this._cancleFilterPromptBtn);
  }

  verifySelectFieldValue(value) {
    expect(this._selectedFilterField.getAttribute('value')).toEqual(value);
  }

  fillFilterOptions(fieldType, operator, value) {
    // Scenario for dates
    if (fieldType === 'date') {
      this.selectPreset(value);
    }
    // Scenario for numbers
    if (fieldType === 'number') {
      this.selectNumberOperatorAndValue(operator, value);
    }

    // Scenario for strings
    if (fieldType === 'string') {
      this.selectStringOperatorAndValue(operator, value);
    }
  }
}
module.exports = FilterDialog;
