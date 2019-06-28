'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SelectedItem = require('../components/SelectedItem');

class ChooseCategory extends SelectedItem {
  constructor() {
    super();
    this._categoryOrMetricName = name =>
      element(by.xpath(`//span[contains(text(),"${name}")]`));
  }

  clickOnCategoryOrMetricName(name) {
    browser.sleep(2000); // Added for bamboo : SIP-7298
    commonFunctions.clickOnElement(this._categoryOrMetricName(name));
  }
}

module.exports = ChooseCategory;
