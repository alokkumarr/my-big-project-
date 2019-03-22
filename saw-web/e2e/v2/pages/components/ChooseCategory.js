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
    commonFunctions.clickOnElement(this._categoryOrMetricName(name));
  }
}

module.exports = ChooseCategory;
