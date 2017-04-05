module.exports = {
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    cardTitle: element(by.binding('::$ctrl.model.name')),
    firstMetric: element(by.xpath('//span[. = "Metric a 1"]/../..')),
    secondMetric: element(by.xpath('//span[. = "Metric b 2"]/../..')),
    reportType: element(by.xpath('//p[. = "Report"]/..')),
    pivotType: element(by.xpath('//p[. = "Pivot"]/..')),
    columnType: element(by.xpath('//p[. = "Column Chart"]/..')),
    lineType: element(by.xpath('//p[. = "Line Chart"]/..')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    designerView: element(by.css('.ard_canvas')),
    columnChartsView: element(by.css('.highcharts-container ')),
    saveReportBtn: element(by.xpath('//button[. = "Save"]')),
    reportCategory: element(by.model('::$ctrl.model.category')),
    reportNameField: element(by.model('$ctrl.model.name')),
    reportDescriptionField: element(by.model('$ctrl.model.description')),
    reportDescription: element(by.model('$ctrl.dataHolder.description')),
    saveReportDetails: element(by.css('[ng-click="$ctrl.save()"]')),
    reportTitle: element(by.css('.e2e-report-title')),
    reportDescriptionBtn: element(by.partialButtonText('Description')),
    reportFilterBtn: element(by.css('[ng-click="$ctrl.openFilterSidenav()"]')),
    filterItemInternet: element(by.css('[aria-label="INTERNET"]')),
    filterItemComplete: element(by.css('[aria-label="Complete"]')),
    applyFilterBtn: element(by.css('[ng-click="$ctrl.onFiltersApplied()"]')),
    filterCounter: element(by.css('.filter-counter')),
    totalPriceField: element(by.css('.e2e-Orders\\:TotalPrice')),
    shipperNameField: element(by.css('.e2e-Shippers\\:ShipperName')),
    customerNameField: element(by.css('.e2e-Customers\\:CustomerName')),
    ordersCustomer: element(by.css('.e2e-Orders\\:CUSTOMER')),
    ordersOrderNumber: element(by.css('.e2e-Orders\\:ORDER_NUMBER')),
    customersEmail: element(by.css('.e2e-Customers\\:EMAIL_ADDRESS')),
    productsProductTypes: element(by.css('.e2e-Products\\:PRODUCT_TYPES')),
    serviceProductStatus: element(by.css('.e2e-Service\\:PROD_OM_STATUS')),
    toggleDetailsPanel: element(by.css('[ng-click="$ctrl.toggleDetailsPanel()"]')),
    reportGridContainer: element(by.css('.ard_details-grid'))
  },

  validateCardView() {
    expect(this.analysisElems.cardView.getAttribute('aria-checked')).toEqual('true');
  },

  validateNewAnalyze() {
    expect(this.analysisElems.newAnalyzeDialog.isDisplayed()).toBeTruthy();
  },

  validateDesignerView() {
    expect(this.analysisElems.designerView.isDisplayed()).toBeTruthy();
  },

  validateColumnChartsView() {
    expect(this.analysisElems.columnChartsView.isDisplayed()).toBeTruthy();
  },

  validateReportGrid() {
    expect(this.analysisElems.reportGridContainer.isDisplayed()).toBeTruthy();
  },

  validateReportFilters() {
    expect(this.analysisElems.filterCounter.isDisplayed()).toBeTruthy();
  }
};
