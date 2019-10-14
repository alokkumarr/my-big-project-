'use-strict';
const commonFunctions = require('../utils/commonFunctions');

class CreateAnalysisModel {
  constructor() {
    // Initialize all elements present in the create analysis model
    this._analysisType = type =>
      element(by.css(`[e2e="choice-group-item-type-${type}"]`));
    this._nextButton = element(by.css('[e2e="create-analysis-btn"]'));
    this._createButton = this._nextButton;
    this._dataPods = name => element(by.css(`[e2e="metric-name-${name}"]`));
    this._chartType = chartType =>
      element(by.css(`[e2e="choice-group-item-subtype-${chartType}"]`));
    this._backButton = element(by.css('[e2e="create-analysis-back-button"]'));
    this._geoLocationType = element(
      by.xpath(`//*[@e2e="choice-group-item-type-"][2]`)
    );
  }

  clickOnAnalysisType(type) {
    if (type) {
      commonFunctions.clickOnElement(this._analysisType(type));
    } else {
      commonFunctions.clickOnElement(this._geoLocationType);
    }
    browser.sleep(2000); // Required somehow in headless mode without sleep its not working. Need to find
  }

  clickOnNextButton() {
    commonFunctions.clickOnElement(this._nextButton);
  }

  clickOnDataPods(name) {
    browser.sleep(2000); // Required somehow in headless mode without sleep its not working. Need to find
    commonFunctions.waitFor.elementToBeClickable(this._backButton);
    commonFunctions.clickOnElement(this._dataPods(name));
  }

  clickOnCreateButton() {
    commonFunctions.clickOnElement(this._createButton);
  }

  clickOnChartType(type) {
    commonFunctions.clickOnElement(this._chartType(type));
  }
}
module.exports = CreateAnalysisModel;
