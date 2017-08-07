const analysisCards = element.all(by.css('md-card[e2e="analysis-card"]'));
const getCards = name => analysisCards.filter(elem => {
  return elem.element(by.css('a[e2e="analysis-name"]')).getText()
    .then(text => {
      return text && text.includes(name);
    });
});

const getCard = name => getCards(name).first();

const getCardTitle = name => element.all(by.cssContainingText('a[e2e="analysis-name"]', name)).first();

const doAnalysisAction = (name, action) => {
  const card = getCard(name);
  const toggle = card.element(by.css('button[e2e="actions-menu-toggle"]'));
  toggle.click();
  toggle.getAttribute('aria-owns').then(id => {
    element(by.id(id)).element(by.css(`button[e2e="actions-menu-selector-${action}"]`)).click();
  });
};

module.exports = {
  newDialog: {
    getMetric: name => element(by.css(`md-radio-button[e2e="metric-name-${name}"]`)),
    getMethod: name => element(by.css(`button[e2e="item-type-${name}"]`)),
    createBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]'))
  },
  designerDialog: {
    elem: element(by.css('analyze-save-dialog')),
    chart: {
      container: element(by.css('.highcharts-container '))
    },
    saveBtn: element(by.css('button[e2e="open-save-modal"]'))
  },
  main: {
    categoryTitle: element((by.css('span[e2e="category-title"]'))),
    getAnalysisCard: getCard,
    getAnalysisCards: getCards,
    getCardTitle,
    doAnalysisAction,
    confirmDeleteBtn: element(by.cssContainingText('button[ng-click="dialog.hide()"]', 'Delete'))
  },
  saveDialog: {
    selectedCategory: element(by.css('md-select[e2e="save-dialog-selected-category"]')),
    nameInput: element(by.css('input[e2e="save-dialog-name"]')),
    descriptionInput: element(by.css('textarea[e2e="save-dialog-description"]')),
    saveBtn: element(by.css('button[e2e="save-dialog-save-analysis"]')),
    cancelBtn: element(by.css('button[translate="CANCEL"]'))
  },
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.partialButtonText('ANALYSIS')),
    cardTitle: element(by.binding('::$ctrl.model.name')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    designerDialog: element(by.css('.ard_canvas')),
    // analysis designer action buttons
    editDescriptionAnalysisBtn: element(by.css('button[e2e="open-description-modal"]')),
    previewAnalysisBtn: element(by.css('[e2e="open-preview-modal"]')),
    filterAnalysisBtn: element(by.css('[e2e="open-filter-modal"]')),
    sortAnalysisBtn: element(by.css('[e2e="open-sort-modal"]')),
    //
    // eventsMetric: element(by.cssContainingText('[ng-model="$ctrl.selectedMetric"]', 'MCT Events')),
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

  validateDesignerDialog() {
    expect(this.analysisElems.designerDialog.isDisplayed()).toBeTruthy();
  },

  validateDesignerView() {
    expect(this.analysisElems.designerView.isDisplayed()).toBeTruthy();
  },

  validateReportGrid() {
    expect(this.analysisElems.reportGridContainer.isDisplayed()).toBeTruthy();
  },

  validateReportFilters() {
    expect(this.analysisElems.filterCounter.isDisplayed()).toBeTruthy();
  }
};
