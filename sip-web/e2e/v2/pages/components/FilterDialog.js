'use strict';

const commonFunctions = require('../utils/commonFunctions');

class FilterDialog {
  constructor() {
    this._addFilter = element(by.css(`[e2e="add-new-filter"]`));
    this._filterColumnDropDown = element(
      by.css('[e2e="filter-columns"]')
    );
    this._columnNameDropDownItem = columnName =>
      element(
        by.xpath(`(//mat-option/span[contains(text(),"${columnName}")])[1]`)
      );
    // Date
    this._filterPresetDropDown = element(
      by.css('[e2e="filter-date-preset"]')
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
    this._applyFiltersBtn = element(by.css(`button[e2e="save-attributes-btn"]`));

    this._promptCheckBox = element(
      by.css(`mat-checkbox[e2e="filter-dialog-prompt-checkbox"]`)
    );
    this._filterDialogText = element(
      by.css(`[e2e="filter-dialog-header-text"]`)
    );

    this._cancleFilterPromptBtn = element(
      by.css(`[e2e="designer-dialog-cancel"]`)
    );
    this._selectedFilterField = value =>element(
      by.xpath(`//*[@class="mat-select-value"]/following::span[text()='${value}']`)
    );
    this._allFilterButton=element(by.xpath(`//button[contains(*,'All')]`));
    this._selectFilterField = value => element(by.css(`[e2e="add-${value}"]`));
    this._tableArtifacts = element(by.css(`mat-select[e2e="filter-artifacts"]`));
    this._previewExpression = element(
        by.cssContainingText('mat-panel-title','Preview Expression'
        )
    );
  }

  clickOnAddFilterButtonByField(fieldName) {
    commonFunctions.waitFor.elementToBePresent(this._filterDialogText);
    commonFunctions.waitFor.elementToBePresent(this._cancleFilterPromptBtn);
    commonFunctions.waitFor.elementToBePresent(this._applyFiltersBtn);
    commonFunctions.clickOnElement(this._addFilter);
    commonFunctions.clickOnElement(this._selectFilterField(fieldName));
    commonFunctions.waitFor.elementToBePresent(this._tableArtifacts);
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
  }

  clickOnColumnInput() {
    commonFunctions.clickOnElement(this._filterColumnDropDown);
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
  }

  clickOnColumnDropDown(name) {
    commonFunctions.clickOnElement(this._columnNameDropDownItem(name));
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
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
      browser.actions().sendKeys(protractor.Key.ENTER).perform();
    } else {
      commonFunctions.fillInput(this._filterStringInput, value);
      browser.actions().sendKeys(protractor.Key.ENTER).perform();
    }
  }

  clickOnApplyFilterButton() {
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.clickOnElement(this._previewExpression);
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
    commonFunctions.clickOnElement(this._applyFiltersBtn);
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
  }

  clickOnPromptCheckBox() {
    commonFunctions.clickOnElement(this._promptCheckBox);
    browser.sleep(2000); // e2e script execution is fast need to wait till element loads in DOM
  }

  shouldFilterDialogPresent() {
    commonFunctions.waitFor.elementToBeVisible(this._filterDialogText);
  }

  clickOnCancelFilterModelButton() {
    commonFunctions.clickOnElement(this._cancleFilterPromptBtn);
  }

  verifySelectFieldValue(value) {
    commonFunctions.waitFor.elementToBePresent(this._selectedFilterField(value));
  }

  fillFilterOptions(fieldType, operator, value) {
    // Scenario for date
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
