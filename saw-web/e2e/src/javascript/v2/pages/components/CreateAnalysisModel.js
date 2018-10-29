'use-strict'
const commonFunctions = require('../../../helpers/commonFunctions');

class CreateAnalysisModel {

  constructor() {
    // Initialize all elements present in the create analysis model
    this.analysisType = (type) => element(by.css(`[e2e="choice-group-item-type-${type}"]`));
    this.nextButton = element(by.css('[e2e="create-analysis-btn"]'));
    this.createButton = this.nextButton;
    this.dataPods = (name) => element(by.css(`[e2e="metric-name-${name}"]`));

  }

  clickOnAnalysisType(type){
    commonFunctions.waitFor.elementToBeVisible(this.analysisType(type));
    this.analysisType(type).click();
  }

  clickOnNextButton(){
    commonFunctions.waitFor.elementToBeVisible(this.nextButton);
    this.nextButton.click();
  }

  clickOnDataPods(name){
    commonFunctions.waitFor.elementToBeVisible(this.dataPods(name));
    this.dataPods(name).click();
  }

  clickOnCreateButton(){
    commonFunctions.waitFor.elementToBeVisible(this.createButton);
    this.createButton.click();
  }


}
module.exports = CreateAnalysisModel

