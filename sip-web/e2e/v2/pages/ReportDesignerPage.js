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
}
module.exports = ReportDesignerPage;
