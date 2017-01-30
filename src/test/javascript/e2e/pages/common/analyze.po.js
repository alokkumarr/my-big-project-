module.exports = {
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    firstMetric: element(by.xpath('//span[. = "Metric a 1"]/../..')),
    secondMetric: element(by.xpath('//span[. = "Metric b 2"]/../..')),
    reportTable: element(by.xpath('//p[. = "Report"]/..')),
    pivotTable: element(by.xpath('//p[. = "Pivot"]/..')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    designerDialog: element(by.css('.ard_canvas')),
    saveReportBtn: element(by.css('[ng-click="$ctrl.openSaveModal()"]'))
  },

  validateCardView() {
    expect(this.analysisElems.cardView.getAttribute('aria-checked')).toEqual('true');
  },

  validateNewAnalyze() {
    expect(this.analysisElems.newAnalyzeDialog.isDisplayed()).toBeTruthy();
  },

  validateDesignerDialog() {
    expect(this.analysisElems.designerDialog.isDisplayed()).toBeTruthy();
  }
};
