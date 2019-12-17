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

    this._attributeIcon = name =>
      element(
        by.xpath(
          `//span[@class="column-name" and text()="${name}"]/following-sibling::mat-icon`
        )
      );
    this._aggregationOption = element(
      by.xpath(`//*[contains(text(),'Aggregation')]`)
    );
    this._aggregation = name => element(by.xpath(`//span[text()="${name}"]`));
    this._queryBtn = element(by.css(`[e2e='e2e-query-btn']`));
    this._confirmOkBtn = element(by.css(`[e2e='confirm-dialog-ok-btn']`));
    this._querySubmitButton = element(by.css(`[e2e='e2e-query-submit']`));
    this._rows = element(by.xpath(`(//tbody)[2]/tr`));
    this._totalRows = element.all(by.xpath(`(//tbody)[2]/tr`));
    this._rowData = (row, col) =>
      element(by.xpath(`(//tbody)[2]/tr[${row}]/td[${col}]`));
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

  /**
     * aggregate": {
          "designerLabel": "MAX",
          "value": "max",
          "field":"Integer"
        }
     */
  applyAggregate(aggregate) {
    commonFunctions.elementToBeClickableAndClickByMouseMove(
      this._attributeIcon(aggregate.field)
    );
    commonFunctions.elementToBeClickableAndClickByMouseMove(
      this._aggregationOption
    );
    commonFunctions.elementToBeClickableAndClickByMouseMove(
      this._aggregation(aggregate.designerLabel)
    );
  }

  fillAnalysisDetailsAndCreate(tables, reportName, reportDescription) {
    this.clickOnReportFields(tables);
    this.verifyDisplayedColumns(tables);
    this.clickOnSave();
    this.enterAnalysisName(reportName);
    this.enterAnalysisDescription(reportDescription);
    this.clickOnSaveAndCloseDialogButton(/analyze/);
  }

  clickOnQueryTab() {
    commonFunctions.clickOnElement(this._queryBtn);
  }
  fillQuery(query) {
    browser
      .actions()
      .mouseMove($('.ace_active-line'))
      .click()
      .sendKeys(protractor.Key.chord(protractor.Key.COMMAND, 'a'))
      .sendKeys(protractor.Key.BACK_SPACE)
      .sendKeys(query)
      .perform();
  }

  clickOnQuerySubmitButton() {
    commonFunctions.clickOnElement(this._querySubmitButton);
  }

  clickOnConfirmButton() {
    commonFunctions.clickOnElement(this._confirmOkBtn);
  }

  verifyRowsDisplayed(rows, columnData = null, sort = null) {
    let self = this;
    commonFunctions.waitFor.elementToBeVisible(this._rows, 120000); //120 sec is max it can be found before that
    expect(this._totalRows.count()).toBe(rows + 1);
    if (columnData) {
      // check first column data of all the rows
      for (let i = 1; i <= rows; i++) {
        expect(this._rowData(i, 1).getText()).toBe(columnData);
      }
    }
  }
}
module.exports = ReportDesignerPage;
