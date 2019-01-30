'use strict';
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
class EditPage {
  constructor() {
    const getReportFieldCheckbox = (tableName, fieldName) =>
      getReportField(tableName, fieldName).element(by.css('mat-checkbox'));
    const getReportField = (tableName, fieldName) =>
      element(by.css(`[e2e="js-plumb-field-${tableName}:${fieldName}"]`));
  }

  selectFields(tables) {
    tables.forEach(table => {
      table.fields.forEach(field => {
        browser.executeScript(
          'arguments[0].scrollIntoView()',
          reportDesigner.getReportFieldCheckbox(table.name, field)
        );
        commonFunctions.waitFor.elementToBeClickable(
          reportDesigner.getReportFieldCheckbox(table.name, field)
        );
        reportDesigner.getReportFieldCheckbox(table.name, field).click();
      });
    });
  }
  // Select fields and refresh
}
module.exports = EditPage;
