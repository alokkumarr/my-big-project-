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

}
module.exports = ChartsDesignerPage;
