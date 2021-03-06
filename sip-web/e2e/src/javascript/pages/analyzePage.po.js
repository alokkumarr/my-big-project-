const {
  doMdSelectOption,
  getMdSelectOptions,
  getMdSelectOptionsNew
} = require('../helpers/utils');
const commonFunctions = require('../helpers/commonFunctions.js');
const protractorConf = require('../../../protractor.conf');

const getCards = name =>
  element.all(by.css('mat-card[e2e="analysis-card"]')).filter(elem => {
    return elem.element(by.cssContainingText('a[e2e="analysis-name"]', name));
  });

const getCard = name => getCards(name).first();

const firstCard = element.all(by.css('mat-card[e2e="analysis-card"]')).first();

const getForkBtn = parent =>
  parent.element(by.css('button[e2e="action-fork-btn"]'));

const getCardTitle = name =>
  element(by.xpath(`//a[@e2e="analysis-name" and text() = "${name}"]`));
const firstCardTitle = element.all(by.css('a[e2e="analysis-name"]')).first();

/**
 * returns the type of the card analysis in a promise, in the form of
 * type:chartType if it has a chartType, if not, it just returns type
 * example: for pivot analysis it returns pivot
 *          for column chart analysis it returns chart:column
 */
const getCardTypeByName = name =>
  getCard(name)
    .element(by.css('[e2e*="analysis-type:"]'))
    .getAttribute('e2e')
    .then(e2eAttribute => {
      if (e2eAttribute) {
        const [, type, chartType] = e2eAttribute.split(':');
        return `${type}${chartType ? `:${chartType}` : ''}`;
      }
      return e2eAttribute;
    });

//Can be used in design mode
const getAnalysisChartType = () =>
  element(by.css('[e2e*="chart-type:'))
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
  commonFunctions.waitFor.elementToBeVisible(toggle);
  commonFunctions.waitFor.elementToBeClickable(toggle);
  toggle.click();
  const menuItems = element(
    by.xpath('//div[contains(@class,"mat-menu-panel")]/parent::div')
  );
  menuItems.getAttribute('id').then(id => {
    if (id) {
      const actionButton = element(by.id(id)).element(
        by.css(`button[e2e="actions-menu-selector-${action}"]`)
      );
      commonFunctions.waitFor.elementToBeVisible(actionButton);
      commonFunctions.waitFor.elementToBeClickable(actionButton);
      actionButton.click();
    } else {
      const menu = card.element(
        by.css('button[e2e="actions-menu-toggle"]' + 'mat-menu')
      );
      menu.getAttribute('class').then(className => {
        commonFunctions.waitFor.elementToBeVisible(
          element(by.css(`div.${className}`)).element(
            by.css(`button[e2e="actions-menu-selector-${action}"]`)
          )
        );
        commonFunctions.waitFor.elementToBeClickable(
          element(by.css(`div.${className}`)).element(
            by.css(`button[e2e="actions-menu-selector-${action}"]`)
          )
        );
        element(by.css(`div.${className}`))
          .element(by.css(`button[e2e="actions-menu-selector-${action}"]`))
          .click();
      });
    }
    // actionButton.click();
  });
};

const getAnalysisActionOptionsNew = name => {
  return getMdSelectOptionsNew({
    parentElem: getCard(name),
    btnSelector: 'button[e2e="actions-menu-toggle"]'
  });
};
const getAnalysisActionOptions = name => {
  return getMdSelectOptions({
    parentElem: getCard(name),
    btnSelector: 'button[e2e="actions-menu-toggle"]'
  });
};

const getAnalysisOption = (parent, option) =>
  parent.element(by.css(`button[e2e="actions-menu-selector-${option}"]`));

const getAnalysisMenuButton = analysisName =>
  element(
    by.xpath(
      "//a[text()='" +
        analysisName +
        "']/../../../..//*[@e2e='actions-menu-toggle']"
    )
  );

const getChartSettingsItem = (axis, name) => {
  return element(
    by.css(`md-radio-group[ng-model="$ctrl.selected.${axis}"]`)
  ).element(by.css(`md-radio-button[e2e="radio-button-${name}"]`));
};

const getChartSettingsRadio = (axis, name) => {
  return element(
    by.css(`md-radio-group[ng-model="$ctrl.selected.${axis}"]`)
  ).element(by.css(`md-radio-button[e2e="radio-button-${name}"]`));
};

const getChartSettingsCheckBox = name => {
  return element(
    by.xpath(
      `//md-checkbox[@ng-model="attr.checked"]/descendant::*[contains(text(),'${name}')]/parent::*`
    )
  );
};

const filtersBtn = element(
  by.css('button[ng-click="$ctrl.openFiltersModal($event)"]')
);
const filtersBtnUpgraded = element(by.css('button[e2e="open-filter-modal"]'));

const refreshBtn = element(by.css('button[e2e="refresh-data-btn"]'));

const getFilterRow = index =>
  element.all(by.css('analyze-filter-row')).get(index);
const getAllAnalysis = name =>
  element(by.xpath(`//a[@e2e="analysis-name" and contains(text(),"${name}")]`));
const geFilterRowUpgraded = index =>
  element.all(by.css('designer-filter-row')).get(index);

const getFilterAutocomplete = index =>
  getFilterRow(index).element(
    by.css('md-autocomplete[e2e="filter-row"] > md-autocomplete-wrap > input')
  );

const getFilterAutocompleteUpgraded = index =>
  geFilterRowUpgraded(index).element(
    by.css('input[e2e="filter-autocomplete-input"]')
  );

// If filter value type is String
const getStringFilterInput = index =>
  getFilterRow(index)
    .element(by.css('string-filter'))
    .element(by.css('input[aria-label="Chips input."]'));

const getStringFilterInputUpgraded = index =>
  geFilterRowUpgraded(index).element(
    by.css('input[e2e="designer-filter-string-input"]')
  );

// If filter value type is Number
const getNumberFilterInput = index =>
  getFilterRow(index).element(
    by.xpath("//input[@ng-model='$ctrl.tempModel.value']")
  );

const getNumberFilterInputUpgraded = index =>
  geFilterRowUpgraded(index).element(
    by.css('input[e2e="designer-number-filter-input"]')
  );

const getAppliedFilter = name =>
  element(by.css('filter-chips')).element(
    by.cssContainingText('md-chip-template > span', name)
  );

const getAppliedFilterUpgraded = name =>
  element(by.css('filter-chips-u')).element(
    by.cssContainingText('mat-chip', name)
  );

const getPivotField = name =>
  element(by.css(`md-list-item[e2e="pivot-field-${name}"]`));

const getPivotFieldCheckbox = name =>
  getPivotField(name).element(by.css('md-checkbox'));

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

const getReportField = (tableName, fieldName) =>
  element(by.css(`[e2e="js-plumb-field-${tableName}:${fieldName}"]`));
const getReportFieldCheckbox = (tableName, fieldName) =>
  getReportField(tableName, fieldName).element(by.css('mat-checkbox'));
//const getReportFieldCheckbox = (tableName, fieldName) =>
// element(by.xpath(`//md-checkbox/div/span[text()='${fieldName}']/ancestor::*[contains(@e2e, '${tableName}')]`));
const getReportFieldEndPoint = (tableName, fieldName, side) => {
  const endpoints = getReportField(tableName, fieldName).all(
    by.css('js-plumb-endpoint')
  );
  switch (side) {
    case 'left':
      return endpoints.last();
    case 'right':
    default:
      return endpoints.first();
  }
};
const getJoinlabel = (
  tableNameA,
  fieldNameA,
  tableNameB,
  fieldNameB,
  joinType
) => {
  return element(
    by.css(
      `[e2e="join-label-${tableNameA}:${fieldNameA}-${joinType}-${tableNameB}:${fieldNameB}"]`
    )
  );
};

const logOut = () => {
  doMdSelectOption({
    parentElem: element(by.css('header > mat-toolbar')),
    btnSelector: '[e2e="account-settings-menu-btn"]',
    optionSelector: `button[e2e="account-settings-selector-logout"]`
  });
  return browser.wait(() => {
    return browser.getCurrentUrl().then(url => {
      return /login/.test(url);
    });
  }, protractorConf.timeouts.fluentWait);
};

const doAccountAction = action => {
  browser.ignoreSynchronization = false;
  navigateToHome();
  browser.ignoreSynchronization = true;
  doMdSelectOption({
    parentElem: element(by.css('header > mat-toolbar')),
    btnSelector: '[e2e="account-settings-menu-btn"]',
    optionSelector: `button[e2e="account-settings-selector-${action}"]`
  });
  return browser.wait(() => {
    return browser.getCurrentUrl().then(url => {
      return /login/.test(url);
    });
  }, protractorConf.timeouts.fluentWait);
};

function goToHome() {
  browser.get(browser.baseUrl);
  return browser.wait(() => {
    return browser.getCurrentUrl().then(url => {
      return /sip/.test(url);
    });
  }, protractorConf.timeouts.fluentWait);
}

function navigateToHome() {
  browser.get(browser.baseUrl);
  return browser.wait(() => {
    return browser.getCurrentUrl().then(url => {
      return /analyze/.test(url);
    });
  }, protractorConf.timeouts.fluentWait);
}

const selectFields = name => {
  commonFunctions.waitFor.elementToBeVisible(
    this.designerDialog.chart.fieldSearchInput
  );
  this.designerDialog.chart.fieldSearchInput.clear();
  this.designerDialog.chart.fieldSearchInput.sendKeys(name);
  commonFunctions.waitFor.elementToBeClickableAndClick(
    this.getFieldPlusIcon(name)
  );
};

module.exports = {
  goToHome,
  navigateToHome,
  newDialog: {
    getMetricRadioButtonElementForReportByName: name =>
      element(by.xpath(`//*[contains(text(),"${name}")]`)),
    getMetricRadioButtonElementByName: name =>
      element(by.css(`mat-radio-button[e2e="metric-name-${name}"]`)),
    getMetricSelectedRadioButtonElementByName: name =>
      element(
        by.css(`mat-radio-button.mat-radio-checked[e2e="metric-name-${name}"]`)
      ),
    getAnalysisTypeButtonElementByType: name =>
      element(by.css(`[e2e="choice-group-item-type-${name}"]`)),
    createBtn: element(by.css('button[e2e="create-analysis-btn"]'))
  },

  designerDialog: {
    saveDialog: element(by.css('[e2e="save-dialog-save-analysis"]')),
    saveDialogUpgraded: element(by.css('designer-save')),
    chart: {
      getFieldPlusIcon: value =>
        element(
          by.xpath(
            `//button[@e2e="designer-add-btn-${value}"]/descendant::*[@ng-reflect-font-icon="icon-plus"]`
          )
        ),
      getMetricsFields: item =>
        element(
          by.xpath(
            `//span[@class="settings__group__title" and contains(text(),"Metrics")]/parent::*/descendant::*[contains(@e2e,"designer-expandable-field-${item}")]`
          )
        ),
      getDimensionFields: item =>
        element(
          by.xpath(
            `//span[@class="settings__group__title" and contains(text(),"Dimension")]/parent::*/descendant::*[contains(@e2e,"designer-expandable-field-${item}")]`
          )
        ),
      getGroupByFields: item =>
        element(
          by.xpath(
            `//span[@class="settings__group__title" and contains(text(),"Group By")]/parent::*/descendant::*[contains(@e2e,"designer-expandable-field-${item}")]`
          )
        ),
      fieldSearchInput: element(
        by.xpath('//input[contains(@id,"mat-input-")]')
      ),
      selectFields: name => selectFields(name),
      getXRadio: name => getChartSettingsRadio('x', name),
      getYRadio: name => getChartSettingsRadio('y', name),
      getYCheckBox: name => getChartSettingsCheckBox(name),
      getYCheckBoxParent: name =>
        commonFunctions.find.parent(getChartSettingsCheckBox(name)),
      getZRadio: name => getChartSettingsRadio('z', name), // z - size by
      getGroupRadio: name => getChartSettingsRadio('g', name), // g - color by
      container: element(by.css('.highcharts-container ')),
      title: element(by.css('span[e2e="designer-type-chart"]')),
      getAnalysisChartType,
      filterBtn: filtersBtnUpgraded,
      refreshBtn
    },
    pivot: {
      getPivotFieldCheckbox,
      doSelectPivotArea,
      doSelectPivotAggregate,
      doSelectPivotGroupInterval,
      filterBtn: filtersBtnUpgraded,
      refreshBtn
    },
    report: {
      expandBtn: element(by.css('button[e2e="report-expand-btn"]')),
      getReportFieldCheckbox,
      getReportFieldEndPoint,
      getJoinlabel,
      filterBtn: filtersBtnUpgraded,
      gridExpandBtn: element(by.css('button[e2e="report-expand-btn"]')),
      refreshBtn
    },
    saveBtn: element(by.css('button[e2e="designer-save-btn"]')),
    saveOnlyBtn: element(by.css('button[e2e="designer-save-only-btn"]'))
  },
  filtersDialog: {
    getFilterAutocomplete,
    getStringFilterInput,
    getNumberFilterInput,
    applyBtn: element(by.css('button[e2e="apply-filter-btn"]')),
    getAppliedFilter: getAppliedFilterUpgraded
  },
  filtersDialogUpgraded: {
    getFilterAutocomplete: getFilterAutocompleteUpgraded,
    getStringFilterInput: getStringFilterInputUpgraded,
    getNumberFilterInput: getNumberFilterInputUpgraded,
    applyBtn: element(by.css('button[e2e="apply-filter-btn"]')),
    getAppliedFilterUpdated: feild =>
      element(by.xpath(`//mat-chip[contains(text(),"${feild}")]`)),
    getAppliedFilter: getAppliedFilterUpgraded,
    chartSectionWithData: element(
      by.css('[ng-reflect-e2e="chart-type:column"]')
    ),
    noDataInChart: element(by.css('[class="non-ideal-state__message"]')),
    prompt: element(by.xpath(`//span[contains(text(),'Prompt')]/parent::*`))
  },
  appliedFiltersDetails: {
    filterText: element(by.xpath('//span[@class="filter-counter"]')),
    filterClear: element(
      by.xpath('//button[contains(@class,"filter-clear-all")]')
    ),
    selectedFilters: element.all(
      by.xpath('//filter-chips-u/descendant::mat-chip')
    ),
    selectedFiltersText: element(
      by.xpath('//filter-chips-u/descendant::mat-chip')
    )
  },
  detail: {
    getAnalysisChartType
  },
  main: {
    categoryName: element(by.css(`[e2e="category-title"]`)),
    getAllAnalysis,
    actionMenuOptions: element(
      by.xpath('//div[contains(@class,"mat-menu-panel")]')
    ),
    categoryTitle: element(by.css('span[e2e="category-title"]')),
    getAnalysisCard: getCard,
    getAnalysisCards: getCards,
    getCardTitle,
    doAnalysisAction,
    getAnalysisActionOptions,
    getAnalysisActionOptionsNew,
    confirmDeleteBtn: element(by.css('button[e2e="confirm-dialog-ok-btn"]')),
    doAccountAction,
    firstCard,
    getForkBtn,
    getAnalysisOption,
    firstCardTitle,
    getCardTypeByName: getCardTypeByName,
    getAnalysisMenuButton: getAnalysisMenuButton,
    logOut
  },
  saveDialogUpgraded: {
    selectedCategory: element(by.css('[e2e="save-dialog-selected-category"]')),
    nameInput: element(by.css('input[e2e="save-dialog-name"]')),
    descriptionInput: element(
      by.css('textarea[e2e="save-dialog-description"]')
    ),
    saveBtn: element(by.css('button[e2e="save-dialog-save-analysis"]')),
    cancelBtn: element(by.css('button[e2e="designer-dialog-cancel"]'))
  },
  saveDialog: {
    selectedCategory: element(
      by.xpath('//md-select[@e2e="save-dialog-selected-category"]/*/span[1]')
    ),
    selectedCategoryUpdated: element(
      by.xpath(
        '//mat-select[@e2e="save-dialog-selected-category"]/descendant::div[@class="mat-select-arrow-wrapper"]'
      )
    ),
    nameInput: element(by.css('input[e2e="save-dialog-name"]')),
    descriptionInput: element(
      by.css('textarea[e2e="save-dialog-description"]')
    ),
    saveBtn: element(by.css('button[e2e="save-dialog-save-analysis"]')),
    cancelBtn: element(by.css('button[translate="CANCEL"]')),
    selectCategoryToSave: name =>
      element(
        by.xpath(`//mat-option/descendant::span[contains(text(),"${name}")]`)
      )
  },
  prompt: {
    filterDialog: element(by.xpath(`//strong[text()='Filter']`)),
    selectedField: element(by.css(`[e2e="filter-autocomplete-input"]`)),
    cancleFilterPrompt: element(by.css(`button[e2e="designer-dialog-cancel"]`))
  },
  listViewItem: name => element(by.xpath(`//a[contains(text(),"${name}")]`)),

  // OLD test elements
  analysisElems: {
    analysisWithType: (name, type) =>
      element(
        by.xpath(
          `//*[@e2e="analysis-type:${type}"]/parent::*/descendant::a[@e2e="analysis-name" and contains(text(),"${name}")]`
        )
      ),
    listView: element(by.css('[e2e="analyze-list-view"]')),
    cardView: element(by.css('[e2e="analyze-card-view"]')),
    newAnalyzeDialog: element(by.css('.new-analyze-dialog')),
    addAnalysisBtn: element(by.css('[e2e="open-new-analysis-modal"]')),
    cardTitle: element(by.binding('::$ctrl.model.name')),
    createAnalysisBtn: element(by.css('[ng-click="$ctrl.createAnalysis()"]')),
    designerDialog: element(by.css('.ard_canvas')),
    // analysis designer action buttons
    editDescriptionAnalysisBtn: element(
      by.css('button[e2e="open-description-modal"]')
    ),
    previewAnalysisBtn: element(by.css('[e2e="open-preview-modal"]')),
    filterAnalysisBtn: element(by.css('[e2e="open-filter-modal"]')),
    sortAnalysisBtn: element(by.css('[e2e="open-sort-modal"]')),
    //
    // eventsMetric: element(by.cssContainingText('[ng-model="$ctrl.selectedMetric"]', 'MCT Events')),
    reportsMetric: element(
      by.cssContainingText(
        'span[ng-bind="::metric.metricName"]',
        'MBT Reporting'
      )
    ),
    designerView: element(by.css('.ard_canvas')),
    columnChartsView: element(by.css('.highcharts-container ')),
    //
    reportCategory: element(by.model('::$ctrl.model.category')),
    reportNameField: element(by.model('$ctrl.model.name')),
    reportDescriptionField: element(by.model('$ctrl.model.description')),
    reportDescription: element(by.model('$ctrl.dataHolder.description')),
    saveReportDetails: element(by.css('[ng-click="$ctrl.save()"]')),
    reportTitle: element(by.css('.e2e-designer-title')),
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
    toggleDetailsPanel: element(
      by.css('[ng-click="$ctrl.toggleDetailsPanel()"]')
    ),
    reportGridContainer: element(by.css('.ard_details-grid')),
    cardMenuButton: element(by.css('button[e2e="actions-menu-toggle"]'))
  },
  validateCardView() {
    expect(this.analysisElems.cardView.getAttribute('class')).toContain(
      'mat-radio-checked'
    );
  },

  validateListView() {
    expect(this.analysisElems.listView.getAttribute('class')).toContain(
      'mat-radio-checked'
    );
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
