'use strict';
const chai = require('chai');
const assert = chai.assert;
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;

const SaveDialog = require('./components/SaveDialog');

class DesignerPage extends SaveDialog {
  constructor() {
    super();
    this._designerButton = element(by.css(`[id="mat-button-toggle-1-button"]`));
    this._reportField = (tableName, fieldName) =>
      element(by.css(`[e2e="js-plumb-field-${tableName}:${fieldName}"]`));
    this._reportFieldCheckbox = (tableName, fieldName) =>
      this._reportField(tableName, fieldName).element(by.css('mat-checkbox'));
    this._saveButton = element(by.css(`[e2e="designer-save-btn"]`));
    this._selectedField = name =>
      element(by.xpath(`//span[@class="column-name" and text()="${name}"]`));
    this._reportGrid = element(by.xpath(`//report-grid-upgraded`));
  }

  verifyOnDesignerPage() {
    commonFunctions.waitFor.elementToBeVisible(this._designerButton);
  }

  clickOnDesignerButton() {
    commonFunctions.waitFor.elementToBeClickable(this._designerButton);
    this._designerButton.click();
  }

  clickOnReportFields(tables) {
    tables.forEach(table => {
      table.fields.forEach(field => {
        commonFunctions.waitFor.elementToBeClickable(
          this._reportFieldCheckbox(table.name, field)
        );
        this._reportFieldCheckbox(table.name, field).click();
      });
    });
  }

  clickOnSave() {
    commonFunctions.waitFor.elementToBeClickable(this._saveButton);
    this._saveButton.click();
  }

  verifyDisplayedColumns(tables) {
    commonFunctions.waitFor.elementToBeVisible(this._reportGrid);
    tables.forEach(table => {
      table.fields.forEach(field => {
        expect(this._selectedField(field).isDisplayed()).toBe(
          true,
          field + ' should be displayed'
        );
      });
    });
  }
}
module.exports = DesignerPage;
