const metrics = element.all(by.css('[ng-model="$ctrl.selectedMetric"] > md-radio-button'));
const eventsMetric = metrics.filter(elem => {
  return elem(by.css('span[ng-bind="::metric.metricName"]')).getText()
    .then(text => {
      return text && text.includes('MCT Events');
    });
}).first();

module.exports = {
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.css('[ng-click="$ctrl.openNewAnalysisModal()"]')),
    cardTitle: element(by.binding('::$ctrl.model.name')),
    //
    firstMetric: element(by.css('[ng-model="$ctrl.selectedMetric"] > md-radio-button:first-child')),
    lastMetric: element(by.css('[ng-model="$ctrl.selectedMetric"] > md-radio-button:last-child')),
    reportType: element(by.css('[value="table:report"]')),
    pivotType: element(by.css('[value="table:pivot"]')),
    columnType: element(by.css('[value="chart:column"]')),
    lineType: element(by.css('[value="chart:line"]')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    saveAnalysisBtn: element(by.css('[ng-click="$ctrl.openSaveModal()"]')),
    //
    // eventsMetric: element(by.cssContainingText('[ng-model="$ctrl.selectedMetric"]', 'MCT Events')),
    eventsMetric,
    reportsMetric: element(by.cssContainingText('span[ng-bind="::metric.metricName"]', 'MBT Reporting')),
    designerView: element(by.css('.ard_canvas')),
    columnChartsView: element(by.css('.highcharts-container ')),
    //
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
    sessionIdField: element(by.css('.e2e-MCT_SESSION\\:SESSION_ID')),
    totalPriceField: element(by.css('.e2e-Orders\\:TotalPrice')),
    shipperNameField: element(by.css('.e2e-Shippers\\:ShipperName')),
    customerNameField: element(by.css('.e2e-Customers\\:CustomerName')),
    ordersCustomer: element(by.css('.e2e-Orders\\:CUSTOMER')),
    ordersOrderNumber: element(by.css('.e2e-Orders\\:ORDER_NUMBER')),
    customersEmail: element(by.css('.e2e-Customers\\:EMAIL_ADDRESS')),
    productsProductTypes: element(by.css('.e2e-Products\\:PRODUCT_TYPES')),
    serviceProductStatus: element(by.css('.e2e-Service\\:PROD_OM_STATUS')),
    refreshDataBtn: element(by.css('[ng-click="$ctrl.refreshGridData()"]')),
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
