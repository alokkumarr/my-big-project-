'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');
const Constants = require('../helpers/Constants');
const Utils = require('../pages/utils/Utils');
class ChartsDesignerPage extends Designer {
  constructor() {
    super();
    this._filterInput = element(by.css(`[name="filter-settings"]`));
    this._attribute = attribute =>
      element(by.css(`[e2e="designer-add-menu-btn-${attribute}"]`));
    this._attributesCloseIcons = element.all(
      by.css('[fonticon="icon-remove"]')
    );
    this._fieldType = (attribute, name) =>
      element(by.css(`[e2e="designer-add-option-btn-${attribute}-${name}"]`));
    this._unselectedField = name =>
      element(by.css(`[e2e="designer-unselected-field-${name}"]`));
    this._previewBtn = element(by.css('button[e2e="open-preview-modal"]'));
    this._dataOptions = name =>
      element(by.css(`[e2e="designer-data-option-${name}"]`));
    this._groupBySelector = element(by.css(`designer-date-interval-selector`));
    this._groupByOption = name =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(),"${name}")]`
        )
      );
    this._aggregateOption = name => element(by.css(`[e2e="${name}"]`));
    this._selectedField = name =>
      element(by.css(`[e2e="designer-selected-field-${name}"]`));
    this._appliedFilter = filter =>
      element(
        by.xpath(
          `//*[@e2e="designer-applied-filters"]/descendant::*[contains(text(),"${filter}")]`
        )
      );
    this._reportSelectedFilters = element.all(
      by.xpath('//filter-chips-u/descendant::mat-chip')
    );
    this._reportFilterText = element(
      by.xpath('//span[@class="filter-counter"]')
    );
    this._reportFilterClear = element(
      by.xpath('//button[contains(@class,"filter-clear-all")]')
    );
  }

  searchAttribute(attribute) {
    commonFunctions.fillInput(this._filterInput(attribute));
  }

  clickOnAttribute(attribute, type) {
    browser
      .actions()
      .mouseMove(this._unselectedField(attribute))
      .click()
      .perform();

    commonFunctions.waitFor.elementToBeVisible(this._attribute(attribute));
    commonFunctions.clickOnElement(this._attribute(attribute));
    browser.sleep(1000);

    commonFunctions.waitFor.elementToBeVisible(
      this._fieldType(attribute, type)
    );
    commonFunctions.clickOnElement(this._fieldType(attribute, type));
    browser.sleep(1000);
  }

  clearAttributeSelection() {
    //Clear all fields.
    this._attributesCloseIcons.then(function(deleteElements) {
      for (let i = 0; i < deleteElements.length; ++i) {
        commonFunctions.clickOnElement(deleteElements[i]);
        browser.sleep(2000); // sleep for some time to avoid failures
      }
    });
  }

  searchInputPresent() {
    commonFunctions.waitFor.elementToBeVisible(this._filterInput);
  }

  clickOnPreviewButton() {
    commonFunctions.clickOnElement(this._previewBtn);
  }

  clickOnDataOptions(type) {
    commonFunctions.clickOnElement(this._dataOptions(type));
  }

  clickOnGroupBySelector() {
    commonFunctions.clickOnElement(this._groupBySelector);
  }

  clickOnGroupByOption(name) {
    commonFunctions.clickOnElement(this._groupByOption(name));
  }
  clickOnAggregateOption(name) {
    commonFunctions.clickOnElement(this._aggregateOption(name));
  }
  verifySelectedFiledIsPresent(name) {
    commonFunctions.waitFor.elementToBeVisible(this._selectedField(name));
  }

  verifyAppliedFilters(filters) {
    filters.forEach(filter => {
      commonFunctions.waitFor.elementToBeVisible(this._appliedFilter(filter));
    });
  }

  validateReportSelectedFilters(filters) {
    commonFunctions.waitFor.elementToBeVisible(this._reportFilterText);
    commonFunctions.waitFor.elementToBeVisible(this._reportFilterClear);
    commonFunctions.waitFor.elementToBeVisible(this._reportSelectedFilters);
    this._reportSelectedFilters
      .map(function(elm) {
        return elm.getText();
      })
      .then(function(displayedFilters) {
        expect(
          Utils.arrayContainsArray(displayedFilters, filters)
        ).toBeTruthy();
      });
  }

  /**
   * analysisType: CHART/PIVOT/REPORT/ES_REPORT
   * filters: array of string
   */
  validateAppliedFilters(analysisType, filters) {
    browser.sleep(1000);
    if (analysisType === Constants.CHART || analysisType === Constants.PIVOT) {
      this.verifyAppliedFilters(filters);
    } else if (
      analysisType === Constants.REPORT ||
      analysisType === Constants.ES_REPORT
    ) {
      this.validateReportSelectedFilters(filters);
    }
  }
}
module.exports = ChartsDesignerPage;
