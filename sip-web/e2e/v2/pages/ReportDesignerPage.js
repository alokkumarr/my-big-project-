'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');

class ReportDesignerPage extends Designer {
  constructor() {
    super();

    this._reportField = (tableName, fieldName) =>
      element(by.css(`[e2e="js-plumb-field-${tableName}:${fieldName}"]`));

    this._reportFieldCheckbox = (tableName, fieldName) =>
      this._reportField(tableName, fieldName).element(by.css('mat-checkbox'));

    this._selectedField = name =>
      element(by.xpath(`//span[@class="column-name" and text()="${name}"]`));

    this._reportGrid = element(by.xpath(`//report-grid-upgraded`));
  }

  clickOnReportFields(tables) {
    tables.forEach(table => {
      table.fields.forEach(field => {
        commonFunctions.clickOnElement(
          this._reportFieldCheckbox(table.name, field)
        );
      });
    });
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
module.exports = ReportDesignerPage;
