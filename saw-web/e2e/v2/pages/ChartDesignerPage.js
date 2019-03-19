'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');

class ChartsDesignerPage extends Designer {
  constructor() {
    super();
    this._filterInput = element(by.css(`[name="filter-settings"]`));
    this._attribute = attribute =>
      element(by.css(`[e2e="designer-add-menu-btn-${attribute}"]`));
    this._attributesCloseIcons = element.all(by.css('[fonticon="icon-remove"]'));
    this._fieldType = (attribute, name) =>
      element(by.css(`[e2e="designer-add-option-btn-${attribute}-${name}"]`));
    this._unselectedField = name =>
      element(by.css(`[e2e="designer-unselected-field-${name}"]`));
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
}
module.exports = ChartsDesignerPage;
