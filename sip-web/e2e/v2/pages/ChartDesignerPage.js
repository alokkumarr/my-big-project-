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
    this._selectedFiltersText = element(
      by.xpath('//filter-chips-u/descendant::mat-chip')
    );
    this._reportFilterText = element(
      by.xpath('//span[@class="filter-counter"]')
    );
    this._reportFilterClear = element(
      by.xpath('//button[contains(@class,"filter-clear-all")]')
    );

    this._selectAndChooseAggregate = aggregateName =>
      element(by.css(`button[e2e=${aggregateName}]`));

    this._openChartSettings = element(
      by.xpath('//button[contains(@class,"options-panel-toggle__btn")]')
    );

    this._aggregateDataOptions = text =>
      element(
        by.css(`mat-expansion-panel[e2e="designer-data-option-${text}"]`)
      );

    this._verifyMetricAggregate = (metric, text) =>
      element(
        by.xpath(
          `//*mat-chip[e2e="designer-selected-field-'${metric}'"]/descendant::*[contains(text(),"${text}")]`
        )
      );
    this._regionInput = element(by.css(`[formcontrolname='regionCtrl']`));
    this._regionByValue = region =>
      element(by.xpath(`//span[contains(text(),'${region}')]`));
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
    commonFunctions.waitFor.elementToBeVisible(this._selectedFiltersText);
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

  clickOnSelectAndChooseAggregate(name) {
    browser.sleep(1000); // Somehow aggregate button was not able to load the aggregate list. So put the browser to sleep.
    // For aggregate chooser component commonfunction is not able to make the element visible. So using the browser to identify.
    browser
      .actions()
      .mouseMove(this._selectAndChooseAggregate(name))
      .click()
      .perform();
  }

  validateSelectedAggregate(metric, designerLabelText, buttonText) {
    expect(this._verifyMetricAggregate(metric, designerLabelText)).toBeTruthy();
    expect(this._selectAndChooseAggregate(buttonText)).toBeTruthy();
  }

  selectRegion(region) {
    commonFunctions.clickOnElement(this._regionInput);
    commonFunctions.clickOnElement(this._regionByValue(region));
  }
}
module.exports = ChartsDesignerPage;
