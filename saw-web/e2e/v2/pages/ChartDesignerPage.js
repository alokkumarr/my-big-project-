'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');

class ChartsDesignerPage extends Designer {
  constructor() {
    super();
    this._filterInput = element(by.css(`[name="filter-settings"]`));
    this._attribute = attribute =>
      element(by.css(`[e2e="designer-add-btn-${attribute}"]`));
    this.attributesCloseIcons = element.all(by.css('[fonticon="icon-close"]'));
    this._expandField = fieldName =>
      element(
        by.xpath(
          `(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`
        )
      );

    this._groupIntervalDropDown = element(
      by.xpath(`//mat-label[contains(text(),"Group interval")]/parent::label`)
    );
    this._groupIntervalDrop = id =>
      element(by.xpath(`//mat-select[@aria-labelledby="${id}"]`));
    this._groupIntervalDropDownElement = groupIntervalName =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(), '${groupIntervalName}')]`
        )
      );
    this._aggregateItem = aggregateFunction =>
      element(by.css(`[e2e="${aggregateFunction}"]`));
  }

  searchAttribute(attribute) {
    commonFunctions.fillInput(this._filterInput(attribute));
  }

  clickOnAttribute(attribute) {
    commonFunctions.clickOnElement(this._attribute(attribute));
  }
  clearAttributeSelection() {
    //Clear all fields.
    this.attributesCloseIcons.then(function(deleteElements) {
      for (let i = 0; i < deleteElements.length; ++i) {
        commonFunctions.clickOnElement(deleteElements[i]);
        browser.sleep(2000); // sleep for some time to avoid failures
      }
    });
  }

  clickOnExpandField(fieldName) {
    commonFunctions.clickOnElement(this._expandField(fieldName));
  }

  selectGroupInterval(name) {
       commonFunctions.waitFor.elementToBeVisible(this._groupIntervalDropDown);
       browser.sleep(2000);
        this._groupIntervalDropDown.getAttribute('id').then(function(id) {
        commonFunctions.waitFor.elementToBeVisible(this._groupIntervalDrop(id));
        commonFunctions.scrollIntoView(this._groupIntervalDrop(id));
        this._groupIntervalDrop(id).click();
      commonFunctions.clickOnElement(this._groupIntervalDropDownElement(name));
    });
  }

  clickOnAggregateButton(name) {
    commonFunctions.clickOnElement(this._aggregateItem(name));
  }
  clickOnAggregateFunctionIcon(name) {
    commonFunctions.clickOnElement(this._aggregateItem(name));
  }
}
module.exports = ChartsDesignerPage;
