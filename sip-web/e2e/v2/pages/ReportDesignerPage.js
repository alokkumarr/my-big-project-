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
    this._totalFields = element.all(by.css(`[role="columnheader"]`));
    this._filterIcon = element(by.css(`[fonticon="icon-filter"]`));
    this._aggregatedFilters = element(by.css(`[class="analyze-aggregate-filters ng-star-inserted"]`));
    this._addAggregateFilter = element(by.css(`[e2e="aggregate-filter-add-btn-"]`));
    this._aggregateColumn = element(by.css(`[role="listbox"]`));
    this._aggregateOption = element(by.css(`[e2e="filter-aggregate-select"]`));
    this._aggregateOperator = element(by.css(`[e2e="filter-number-operator-select"]`));
    this._aggregateOperatorValue = element(by.css(`[e2e="designer-number-filter-input"]`));
    this._applyAggregateFilter = element(by.css(`[e2e="save-attributes-btn"]`));
    this._selectOption = value => element(
        by.xpath(
            `//span[@class="mat-option-text" and contains(text(),'${value}')]`
        )
    );
    this._verifyAppliedAggregateFilter = value => element(by.css(`[e2e="filters-execute-${value}"]`));
    this._removeAggregateFilter = value => this._verifyAppliedAggregateFilter(value).element(
      by.css(
        `[fonticon="icon-remove"]`
      )
    );
    this._previewExpression = element(
      by.cssContainingText('mat-panel-title','Preview Expression'
      )
    );
    this._betOperatorFirstValue = element(by.xpath(`(//input[@type="number"])[1]`));
    this._betOperatorSecondValue = element(by.xpath(`(//input[@type="number"])[2]`));
    this._refreshData = element(
      by.xpath(`//span[text()='Refresh Data']`
      )
    );
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

  verifySelectedFieldsCount(count) {
    browser.sleep(2000); //Need to wait till grid loads
    expect(this._totalFields.count()).toBe(count);
  }

  clickFilter() {
    commonFunctions.clickOnElement(this._filterIcon);
  }

  verifyAggregateFilters() {
    commonFunctions.waitFor.elementToBeVisible(this._aggregatedFilters);
  }

  verifyAppliedAggregateFilter(value) {
    commonFunctions.waitFor.elementToBeVisible(this._verifyAppliedAggregateFilter(value))
  }

  removeAggregateAndVerify(value) {
    commonFunctions.clickOnElement(this._removeAggregateFilter(value));
    commonFunctions.waitFor.elementToBeNotVisible(this._verifyAppliedAggregateFilter(value));
  }

  applyNewAggregateFilter(aggregateFilters) {
    this.clickFilter();
    this.verifyAggregateFilters();
    commonFunctions.clickOnElement(
      this._addAggregateFilter
    );
    commonFunctions.clickOnElement(
      this._aggregateColumn
    );
    commonFunctions.clickOnElement(
      this._selectOption(aggregateFilters.field)
    );
    commonFunctions.clickOnElement(
      this._aggregateOption
    );
    commonFunctions.clickOnElement(
      this._selectOption(aggregateFilters.designerLabel)
    );
    commonFunctions.clickOnElement(
      this._aggregateOperator
    );
    commonFunctions.clickOnElement(
      this._selectOption(aggregateFilters.operator)
    );
    if(aggregateFilters.operator === "Between") {
      commonFunctions.fillInput(this._betOperatorFirstValue,aggregateFilters.firstValue);
      commonFunctions.fillInput(this._betOperatorSecondValue,aggregateFilters.secondValue);
    }else {
      commonFunctions.fillInput(
        this._aggregateOperatorValue,aggregateFilters.operatorValue
      );
    }
    commonFunctions.clickOnElement(this._applyAggregateFilter);
    browser.sleep(2000); //Need to wait till result grid refresh with new filters
  }

  refreshAnalysis() {
    commonFunctions.clickOnElement(this._refreshData);
  }
}
module.exports = ReportDesignerPage;
