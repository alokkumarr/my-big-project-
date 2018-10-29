'use-strict'

let CreateAnalysisModel = require('./components/CreateAnalysisModel');
const commonFunctions = require('../../helpers/commonFunctions');

class AnalyzePage extends CreateAnalysisModel{

  constructor() {
    super();
    // Add all the elements in this page related to analyze page
    // e.g. listView/CardView/List analysis based on Type, addAnalysis button etc
    this.addAnalysisButton=element(by.css('[e2e="open-new-analysis-modal"]'));
  }

  clickOnAddAnalysisButton() {
    commonFunctions.waitFor.elementToBeVisible(this.addAnalysisButton);
    this.addAnalysisButton.click();
  }
}

module.exports= AnalyzePage;
