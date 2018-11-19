'use-strict'

let CreateAnalysisModel = require('./components/CreateAnalysisModel');
const commonFunctions = require('../../helpers/commonFunctions');

class AnalyzePage extends CreateAnalysisModel{

  constructor() {
    super();
    // Add all the elements in this page related to analyze page
    // e.g. listView/CardView/List analysis based on Type, addAnalysis button etc
    this._addAnalysisButton=element(by.css('[e2e="open-new-analysis-modal"]'));
  }

  clickOnAddAnalysisButton() {
    commonFunctions.waitFor.elementToBeVisible(this._addAnalysisButton);
    this._addAnalysisButton.click();
  }
}

module.exports= AnalyzePage;
