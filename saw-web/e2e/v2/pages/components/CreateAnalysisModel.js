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
  }

  clickOnAnalysisType(type) {
    commonFunctions.clickOnElement(this._analysisType(type));
  }

  clickOnNextButton() {
    commonFunctions.clickOnElement(this._nextButton);
  }

  clickOnDataPods(name) {
    commonFunctions.waitFor.elementToBeClickable(this._backButton);
    commonFunctions.clickOnElement(this._dataPods(name));
  }

  clickOnCreateButton() {
    commonFunctions.clickOnElement(this._createButton);
    commonFunctions.waitFor.elementToBeClickable(this._createButton);
  }

  clickOnChartType(type) {
    commonFunctions.clickOnElement(his._chartType(type));
  }
}
module.exports = CreateAnalysisModel;
