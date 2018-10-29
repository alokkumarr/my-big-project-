'use-strict'
const commonFunctions = require('../../../helpers/commonFunctions');

class CreateAnalysisModel {

  constructor() {
    // Initialize all elements present in the create analysis model
    this._analysisType = (type) => element(by.css(`[e2e="choice-group-item-type-${type}"]`));
    this._nextButton = element(by.css('[e2e="create-analysis-btn"]'));
    this._createButton = this._nextButton;
    this._dataPods = (name) => element(by.css(`[e2e="metric-name-${name}"]`));
    this._chartType = (chartType) => element(by.css(`[e2e="choice-group-item-subtype-${chartType}"]`));


  }

  clickOnAnalysisType(type){
    commonFunctions.waitFor.elementToBeVisible(this._analysisType(type));
    this._analysisType(type).click();
  }

  clickOnNextButton(){
    commonFunctions.waitFor.elementToBeVisible(this._nextButton);
    this._nextButton.click();
  }

  clickOnDataPods(name){
    commonFunctions.waitFor.elementToBeVisible(this._dataPods(name));
    this._dataPods(name).click();
  }

  clickOnCreateButton(){
    commonFunctions.waitFor.elementToBeVisible(this._createButton);
    this._createButton.click();
  }

  clickOnChartType(type){
    commonFunctions.waitFor.elementToBeVisible(this._chartType(type));
    this._chartType(type).click();
  }


}
module.exports = CreateAnalysisModel

