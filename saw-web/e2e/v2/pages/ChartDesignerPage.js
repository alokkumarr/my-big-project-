'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');

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
    this._filterBtn = element(by.css(`[e2e="open-filter-modal"]`));
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

  clickOnFilterButton() {
    commonFunctions.clickOnElement(this._filterBtn);
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
}
module.exports = ChartsDesignerPage;
