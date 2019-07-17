'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SaveDialog = require('./SaveDialog');
const Utils = require('../utils/Utils');
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
    this._sortField = field => element(by.css(`[e2e='sort-field-${field}']`));
    this._ascSortBtnByField = field =>
      this._sortField(field).element(by.css(`[e2e='sort-asc']`));
    this._descSortBtnByField = field =>
      this._sortField(field).element(by.css(`[e2e='sort-desc']`));
    this._applySortBtn = element(
      by.xpath(`//span[contains(text(),'Apply Sort')]/parent::button`)
    );
    this._addSortFieldBtn = field =>
      element(by.css(`[e2e='sort-add-btn-${field}']`));
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
    browser.sleep(2000); // this need because protarctor is too fast and application is not operating as expected
  }
  clickOnBottomNButton() {
    commonFunctions.clickOnElement(this._bottomNBtn);
    browser.sleep(2000); // this need because protarctor is too fast and application is not operating as expected
  }
  fillLimitValue(value) {
    commonFunctions.fillInput(this._limitValue, value);
    this._limitValue.click();
    browser.sleep(2000); // this need because protarctor is too fast and application is not operating as expected
  }

  fillAliasInput(attr, value) {
    commonFunctions.fillInput(this._aliasInput(attr), value);
  }

  clickOnAscSortButtonByField(field) {
    let _self = this;
    browser.sleep(2000);
    commonFunctions.clickOnElement(this._ascSortBtnByField(field));
  }

  clickOnDescSortButtonByField(field) {
    let _self = this;
    browser.sleep(2000);
    commonFunctions.clickOnElement(this._descSortBtnByField(field));
  }

  clickOnApplySortButton() {
    commonFunctions.clickOnElement(this._applySortBtn);
  }

  clickOnAddToSortButton(field) {
    commonFunctions.clickOnElement(this._addSortFieldBtn(field));
  }
  verifySortOptionsApplied(sorts, type) {
    sorts.forEach(sort => {
      if (type === 'asc') {
        expect(
          Utils.hasClass(
            this._ascSortBtnByField(sort),
            'mat-button-toggle-checked'
          )
        ).toBeTruthy();
      } else if (type == 'desc') {
        expect(
          Utils.hasClass(
            this._descSortBtnByField(sort),
            'mat-button-toggle-checked'
          )
        ).toBeTruthy();
      }
    });
  }
}
module.exports = Designer;
