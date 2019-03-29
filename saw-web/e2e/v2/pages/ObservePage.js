'use strict';

const commonFunctions = require('./utils/commonFunctions');
const DashboardHeader = require('../pages/DashboardHeader');

class ObservePage extends DashboardHeader {
  constructor() {
    super();
    this._dashboardTitle = name =>
      element(by.xpath(`//span[contains(text(),"${name}")]`));
    this._addedAnalysisByName = name =>
      element(by.xpath(`//h1[text()="${name}"]`));
    this._kpiByName = name =>
      element(by.xpath(`//*[contains(text(),"${name}")]`));
    this._filterByName = name =>
      element(
        by.xpath(`//div[contains(text(),"${name}") and @class="filter-label"]`)
      );
  }

  verifyKpiByName(name) {
    expect(this._kpiByName(name).isDisplayed).toBeTruthy();
  }

  verifyFilterByName(name) {
    expect(this._filterByName(name).isDisplayed).toBeTruthy();
  }

  verifyDashboardTitle(title) {
    expect(this._dashboardTitle(title).isDisplayed).toBeTruthy();
  }

  verifyDashboardTitleIsDeleted(dashboardName) {
    commonFunctions.waitFor.elementToBeNotVisible(
      this._dashboardTitle(dashboardName)
    );
    expect(this._dashboardTitle(dashboardName).isPresent()).toBeFalsy();
  }

  verifyBrowserURLContainsText(text) {
    expect(browser.getCurrentUrl()).toContain(text);
  }

  verifyAddedAnalysisName(name) {
    expect(this._addedAnalysisByName(name).isDisplayed).toBeTruthy();
  }
}
module.exports = ObservePage;
