module.exports = {

  analysisElems: {
    analyzeCard: element(by.css('.analyze-card')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    analyzeDialog: element(by.css('.analyze-dialog'))

  },

  validateCard: function () {
    expect(this.analysisElems.analyzeCard.isPresent()).toBeTruthy();
  },

  validateAnalyzeDialog: function () {
    expect(this.analysisElems.analyzeDialog.isPresent()).toBeTruthy();
  }
};
