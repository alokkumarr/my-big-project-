const commonFunctions = require('../../helpers/commonFunctions.js');
const protractorConf = require('../../../../protractor.conf');

module.exports = {
  addDashboardButton: element(by.css('button[e2e="dashboard-new-dashboard-button"]')),
  addWidgetButton: element(by.css('button[e2e="dashboard-add-widget-button"]')),
  existingAnalysisLink: element(by.css('button[e2e="dashboard-add-widget-type-analysis"]')),
  category: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  subCategory: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  addAnalysisByName: name => element(by.xpath(`//strong[text()="${name}"]/parent::*/descendant::button`)),
  addAnalysisById: id => element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`)),
  removeAnalysisById: id => element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`)),
  saveButton: element(by.css('button[e2e="dashboard-designer-save-button"]')),
  categorySelect: element(by.css('[e2e="dashboard-save-category-select"]')),
  dashboardName: element(by.css('input[e2e="dashboard-save-name-input"]')),
  dashboardDesc: element(by.css('textarea[e2e="dashboard-save-description-input"]')),
  subCategorySelect: name => element(by.xpath(`//span[@class="mat-option-text"and contains(text(),"${name}")]`)),
  saveDialogBtn: element(by.css(`[e2e="dashboard-save-save-button"]`)),
  metricFetchProgressBar: element(by.xpath('//div[contains(@class,"mat-progress-bar-element")]')),
  filterButton: element(by.css('button[e2e="dashboard-open-global-filters-button"]')),
  globalFilters: {
    stringFilter: element(by.css(`input[e2e="dashboard-global-filter-string-input"]`)),
    stringFilterValue : value => element(by.xpath(`//mat-option[contains(text(),"${value}")]`)),
    applyFilter: element(by.css(`button[e2e="dashboard-global-filters-apply-button"]`)),
    dateFilterPreset: element(by.css('[e2e="filter-date-preset"]')),
    dateFilterPresetValue: value => element(by.xpath(`//mat-option[contains(text(),"${value}")]`)),
    selectedPreset: element(by.css('[e2e="dasbhoard-global-filter-date-preset-select"]')),
    selectedPresetValue: value => element(by.xpath(`//*[contains(text(),"${value}")]`)),
    numberSlider: element(by.css('[e2e="dashboard-global-filter-number-slider"]')),
    numberSliderLow: element(by.xpath('//*[@e2e="dashboard-global-filter-number-slider"]/descendant::*[contains(@class,"noUi-handle-lower")]')),
    cancelButton: element(by.css('[e2e="dashboard-global-filters-cancel-button"]'))


  },
  dashboard:{
    dashboardTitle: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
    dashboardAction: action => element(by.xpath(`//span[contains(text(),"${action}")]`)),
    dashboardConfirmDeleteButton: element(by.css('[e2e="dashboard-confirm-dialog-confirm"]')),
    refreshButton: element(by.css('[e2e="dashboard-manual-refresh-button"]')),
    deleteButton: element(by.css('[e2e="dashboard-delete-dashboard-button"]')),
    downloadButton: element(by.css('[e2e="dashboard-download-dashboard-button"]')),
    editButton: element(by.css('[e2e="dashboard-edit-dashboard-button"]')),
    addedAnalysisByName : name => element(by.xpath(`//h1[text()="${name}"]`))
  },
  snapshotKPI: {
    snapshotKPILink: element(by.css('button[e2e="dashboard-add-widget-type-kpi"]')),
    metricByName: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
    kpiColumnByName: name => element(by.xpath(`//button[@e2e="dashboard-add-kpi-column-${name}"]`)),
    dateFieldSelect: element(by.css('[e2e="dashboard-add-kpi-date-column-select"]')),
    dateOptionValue: name => element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`)),
    datePreselect: element(by.css('[e2e="dashboard-add-kpi-date-preset-select"]')),
    datePreselectValue: name => element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`)),
    aggregationSelect: element(by.css('[e2e="dashboard-add-kpi-aggregation-select"]')),
    aggregationSelectValue: name => element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`)),
    secondaryAggregateByName: name => element(by.xpath(`//*[@e2e="dashboard-add-kpi-secondary-aggregate-${name}"]`)),
    secondaryAggregateAverage: element(by.css('[e2e="dashboard-add-kpi-secondary-aggregate-Average"]')),
    secondaryAggregateMinimum: element(by.css('[e2e="dashboard-add-kpi-secondary-aggregate-Minimum"]')),
    secondaryAggregateMaximum: element(by.css('[e2e="dashboard-add-kpi-secondary-aggregate-Maximum"]')),
    secondaryAggregateCount: element(by.css('[e2e="dashboard-add-kpi-secondary-aggregate-Count"]')),
    backgroundColorByName: name => element(by.xpath(`//*[@e2e="dashboard-add-kpi-color-${name}"]`)),
    applyKPIButton: element(by.css('button[e2e="dashboard-add-kpi-apply-button"]')),
    kpiName: element(by.css('input[e2e="dashboard-add-kpi-name-input"]')),
    kpiByName: name => element(by.xpath(`//*[contains(text(),"${name}")]`)),
    filterByName: name => element(by.xpath(`//div[contains(text(),"${name}") and @class="filter-label"]`))
  },
  actualVsTargetKPI: {
    metricByName: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
    kpiColumnByName: name => element(by.xpath(`//button[@e2e="dashboard-add-kpi-column-${name}"]`)),
    actualVsTagertKpiButton:element(by.css('button[e2e="dashboard-add-widget-type-bullet-kpi"]')),
    measure1Input: element(by.css('input[e2e="dashboard-add-kpi-bullet-measure1-input"]')),
    measure2Input: element(by.css('input[e2e="dashboard-add-kpi-bullet-measure2-input"]')),
    metricTargetInput: element(by.css('input[e2e="dashboard-add-kpi-bullet-target-input"]')),
    bandColor: name => element(by.css(`[e2e="dashboard-add-kpi-bullet-color-${name}"]`))

  }
};
