const {doMdSelectOption, getMdSelectOptions} = require('../../helpers/utils');
const commonFunctions = require('../../helpers/commonFunctions.js');

const getCards = name => element.all(by.css('md-card[e2e="analysis-card"]')).filter(elem => {
  return elem.element(by.cssContainingText('a[e2e="analysis-name"]', name));
});

const getCard = name => getCards(name).first();

const firstCard = element.all(by.css('md-card[e2e="analysis-card"]')).first();

const getForkBtn = parent => parent.element(by.css('button[e2e="action-fork-btn"]'));

const getCardTitle = name => element(by.xpath(`//a[@e2e="analysis-name" and text() = "${name}"]`));
const firstCardTitle = element.all(by.css('a[e2e="analysis-name"]')).first();

/**
 * returns the type of the card analysis in a promise, in the form of
 * type:chartType if it has a chartType, if not, it just returns type
 * example: for pivot analysis it returns pivot
 *          for column chart analysis it returns chart:column
 */
const getCardTypeByName = name => getCard(name)
  .element(by.css('[e2e*="analysis-type:"]'))
  .getAttribute('e2e')
  .then(e2eAttribute => {
    if (e2eAttribute) {
      const [, type, chartType] = e2eAttribute.split(':');
      return `${type}${cartType ? `:${chartType}` : ''}`;
    }
    return e2eAttribute;
  });

//Can be used in design mode
const getAnalysisChartType = () => element(by.css('[e2e*="chart-type:'))
  .getAttribute('e2e')
  .then(e2e => {
    if (e2e) {
      const [, chartType] = e2e.split(':');
      return chartType;
    }
    return e2e;
  });

const doAnalysisAction = (name, action) => {
  const card = getCard(name);
  const toggle = card.element(by.css('button[e2e="actions-menu-toggle"]'));
  commonFunctions.waitFor.elementToBeClickable(toggle);
  commonFunctions.waitFor.elementToBeVisible(toggle);
  toggle.click();
  toggle.getAttribute('aria-owns').then(id => {
    const actionButton = element(by.id(id))
      .element(by.css(`button[e2e="actions-menu-selector-${action}"]`));
    commonFunctions.waitFor.elementToBeVisible(actionButton);
    actionButton.click();
  });
};

const getAnalysisActionOptions = name => {
  return getMdSelectOptions({
    parentElem: getCard(name),
    btnSelector: 'button[e2e="actions-menu-toggle"]'
  });
};

const getAnalysisOption = (parent, option) => parent.element(by.css(`button[e2e="actions-menu-selector-${option}"]`));

const getChartSettingsRadio = (axis, name) => {
  return element(by.css(`md-radio-group[ng-model="$ctrl.selected.${axis}"]`))
    .element(by.css(`md-radio-button[e2e="radio-button-${name}"]`));
};

const getChartSettingsCheckBox = name => {
  return element(by.xpath(`//md-checkbox[@ng-model="attr.checked"]/*/span[text()="${name}"]/parent::*/preceding-sibling::*`));
};

const openFiltersBtn = element(by.css('button[ng-click="$ctrl.openFiltersModal($event)"]'));

const refreshBtn = element(by.css('button[e2e="refresh-data-btn"]'));

const getFilterRow = index => element.all(by.css('analyze-filter-row')).get(index);

const getFilterAutocomplete = index => getFilterRow(index)
  .element(by.css('md-autocomplete[e2e="filter-row"] > md-autocomplete-wrap > input'));

const getStringFilterInput = index => getFilterRow(index)
  .element(by.css('string-filter'))
  .element(by.css('input[aria-label="Chips input."]'));

const getAppliedFilter = name => element(by.css('filter-chips'))
  .element(by.cssContainingText('md-chip-template > span', name));

const getPivotField = name => element(by.css(`md-list-item[e2e="pivot-field-${name}"]`));

const getPivotFieldCheckbox = name => getPivotField(name).element(by.css('md-checkbox'));

const doSelectPivotArea = (name, area) => {
  doMdSelectOption({
    parentElem: getPivotField(name),
    btnSelector: 'button[e2e="pivot-area-menu-btn"]',
    optionSelector: `button[e2e="pivot-area-selector-${area}"]`
  });
};
const doSelectPivotAggregate = (name, aggregate) => {
  doMdSelectOption({
    parentElem: getPivotField(name),
    btnSelector: 'button[e2e="pivot-aggregate-menu-btn"]',
    optionSelector: `button[e2e="pivot-aggregate-selector-${aggregate}"]`
  });
};
const doSelectPivotGroupInterval = (name, groupInterval) => {
  doMdSelectOption({
    parentElem: getPivotField(name),
    btnSelector: 'button[e2e="pivot-group-interval-menu-btn"]',
    optionSelector: `button[e2e="pivot-group-interval-selector-${groupInterval}"]`
  });
};

const getReportField = (tableName, fieldName) => element(by.css(`[e2e="js-plumb-field-${tableName}:${fieldName}"]`));
const getReportFieldCheckbox = (tableName, fieldName) => getReportField(tableName, fieldName).element(by.css('md-checkbox'));
const getReportFieldEndPoint = (tableName, fieldName, side) => {
  const endpoints = getReportField(tableName, fieldName).all(by.css('js-plumb-endpoint'));
  switch (side) {
    case 'left':
      return endpoints.last();
    case 'right':
    default:
      return endpoints.first();
  }
};
const getJoinlabel = (tableNameA, fieldNameA, tableNameB, fieldNameB, joinType) => {
  return element(by.css(`[e2e="join-label-${tableNameA}:${fieldNameA}-${joinType}-${tableNameB}:${fieldNameB}"]`));
};

const doAccountAction = action => {
  navigateToHome();
  doMdSelectOption({
    parentElem: element(by.css('header > md-toolbar')),
    btnSelector: 'button[e2e="account-settings-menu-btn"]',
    optionSelector: `button[e2e="account-settings-selector-${action}"]`
  });
  return browser.driver.wait(() => {
    return browser.driver.getCurrentUrl().then(url => {
      return /login/.test(url);
    });
  }, 10000);
};

function navigateToHome() {
  browser.driver.get('http://localhost:3000/');
  return browser.driver.wait(() => {
    return browser.driver.getCurrentUrl().then(url => {
      return /analyze/.test(url);
    });
  }, 10000);
};

module.exports = {
  newDialog: {
    getMetric: name => element(by.css(`md-radio-button[e2e="metric-name-${name}"]`)),
    getMethod: name => element(by.css(`button[e2e="item-type-${name}"]`)),
    createBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]'))
  },
  designerDialog: {
    saveDialog: element(by.css('analyze-save-dialog')),
    chart: {
      getXRadio: name => getChartSettingsRadio('x', name),
      getYCheckBox: name => getChartSettingsCheckBox(name),
      getYCheckBoxParent: name => commonFunctions.find.parent(getChartSettingsCheckBox(name)),
      getZRadio: name => getChartSettingsRadio('z', name),
      getGroupRadio: name => getChartSettingsRadio('g', name),
      container: element(by.css('.highcharts-container ')),
      title: element(by.css('span[e2e="designer-type-chart"]')),
      getAnalysisChartType,
      openFiltersBtn,
      refreshBtn
    },
    pivot: {
      title: element(by.css('span[e2e="designer-type-pivot"]')),
      getPivotFieldCheckbox,
      doSelectPivotArea,
      doSelectPivotAggregate,
      doSelectPivotGroupInterval,
      openFiltersBtn,
      refreshBtn
    },
    report: {
      title: element(by.css('span[e2e="designer-type-report"]')),
      expandBtn: element(by.css('button[e2e="report-expand-btn"]')),
      getReportFieldCheckbox,
      getReportFieldEndPoint,
      getJoinlabel,
      openFiltersBtn,
      refreshBtn
    },
    saveBtn: element(by.css('button[e2e="open-save-modal"]'))
  },
  filtersDialog: {
    getFilterAutocomplete,
    getStringFilterInput,
    applyBtn: element(by.css('button[e2e="apply-filter-btn"]')),
    getAppliedFilter
  },
  main: {
    categoryTitle: element((by.css('span[e2e="category-title"]'))),
    getAnalysisCard: getCard,
    getAnalysisCards: getCards,
    getCardTitle,
    doAnalysisAction,
    getAnalysisActionOptions,
    confirmDeleteBtn: element(by.cssContainingText('button[ng-click="dialog.hide()"]', 'Delete')),
    doAccountAction,
    firstCard,
    getForkBtn,
    getAnalysisOption,
    firstCardTitle,
    getCardType: getCardTypeByName
  },
  saveDialog: {
    selectedCategory: element(by.xpath('//md-select[@e2e="save-dialog-selected-category"]/*/span[1]')),
    nameInput: element(by.css('input[e2e="save-dialog-name"]')),
    descriptionInput: element(by.css('textarea[e2e="save-dialog-description"]')),
    saveBtn: element(by.css('button[e2e="save-dialog-save-analysis"]')),
    cancelBtn: element(by.css('button[translate="CANCEL"]'))
  },

  // OLD test elements
  analysisElems: {
    listView: element(by.css('[ng-value="$ctrl.LIST_VIEW"]')),
    cardView: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.css('[ng-click="$ctrl.openNewAnalysisModal()"]')),
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
    refreshDataBtn: element(by.css('[ng-click="$ctrl.onRefreshData()"]')),
    toggleDetailsPanel: element(by.css('[ng-click="$ctrl.toggleDetailsPanel()"]')),
    reportGridContainer: element(by.css('.ard_details-grid'))
  },

  validateCardView() {
    expect(this.analysisElems.cardView.getAttribute('aria-checked')).toEqual('true');
  },

  validateListView() {
    expect(this.analysisElems.listView.getAttribute('aria-checked')).toEqual('true');
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
  },
  navigateToHomePage: () => {
    navigateToHome();
  }
};
