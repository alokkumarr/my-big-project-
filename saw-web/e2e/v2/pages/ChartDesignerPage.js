'use-strict';

const Designer = require('./components/Designer');
const commonFunctions = require('./utils/commonFunctions');

class ChartsDesignerPage extends Designer {
  constructor() {
    super();
    this._filterInput = element(by.css(`[name="filter-settings"]`));
    this._attribute = attribute =>
      element(by.css(`[e2e="designer-add-btn-${attribute}"]`));
  }

  searchAttribute(attribute) {
    commonFunctions.fillInput(this._filterInput(attribute));
  }

  clickOnAttribute(attribute) {
    commonFunctions.clickOnElement(this._attribute(attribute));
  }
}
module.exports = ChartsDesignerPage;
