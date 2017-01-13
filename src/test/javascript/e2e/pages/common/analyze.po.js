module.exports = {
  analysisElems: {
    analyzeCard: element(by.css('.analyze-card')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    analyzeDialog: element(by.css('.analyze-dialog')),
    analyzeReportTable: element(by.xpath('//*[@id=\'dialogContent_11\']/md-dialog-content/analyze-dialog-content/div/div[4]/div[1]/div[2]/choice-group/div/button[1]')),
    createAnalysisBtn: element(by.buttonText('Create Analysis')),
    designerDialog: element(by.css('.analyze-dialog_content'))
  },

  validateCard() {
    expect(this.analysisElems.analyzeCard.isPresent()).toBeTruthy();
  },

  validateAnalyzeDialog() {
    expect(this.analysisElems.analyzeDialog.isPresent()).toBeTruthy();
  },

  designerDialog() {
    expect(this.analysisElems.designerDialog.isPresent()).toBeTruthy();
  }
};
