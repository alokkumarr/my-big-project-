'use strict';
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
  }

  verifyOnDesignerPage() {
    expect(this._designerButton.isDisplayed()).toBeTruthy();
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
}
module.exports = DesignerPage;
