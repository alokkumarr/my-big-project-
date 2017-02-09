module.exports = {
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    reportTitle: element(by.binding('$ctrl.data.title')),
    firstMetric: element(by.xpath('//span[. = "Metric a 1"]/../..')),
    secondMetric: element(by.xpath('//span[. = "Metric b 2"]/../..')),
    reportTable: element(by.xpath('//p[. = "Report"]/..')),
    pivotTable: element(by.xpath('//p[. = "Pivot"]/..')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    designerDialog: element(by.css('.ard_canvas')),
    saveReportBtn: element(by.xpath('//button[. = "Save"]')),
    reportCategory: element(by.model('::$ctrl.dataHolder.category')),
    firstCategoryOption: element(by.css('[value="1"]')),
    reportName: element(by.model('$ctrl.dataHolder.title')),
    reportDescription: element(by.model('$ctrl.dataHolder.description')),
    saveReportDetails: element(by.css('[ng-click="$ctrl.save()"]')),
    reportDescriptionBtn: element(by.partialButtonText('Description')),
    totalPriceField: element(by.css('.e2e-Orders\\:TotalPrice')),
    shipperNameField: element(by.css('.e2e-Shippers\\:ShipperName')),
    customerNameField: element(by.css('.e2e-Customers\\:CustomerName')),
    toggleDetailsPanel: element(by.css('[ng-click="$ctrl.toggleDetailsPanel()"]'))
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
