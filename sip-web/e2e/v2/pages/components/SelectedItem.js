'use strict';

const commonFunctions = require('../utils/commonFunctions');
const SaveDashboardDialog = require('../components/SaveDashboardDialog');

class SelectedItem extends SaveDashboardDialog {
  constructor() {
    super();
    this._addAnalysisById = id =>
      element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`));
    this._removeAnalysisById = id =>
      element(
        by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`)
      );
    this._kpiColumnByName = name =>
      element(by.xpath(`//button[@e2e="dashboard-add-kpi-column-${name}"]`));
    this._kpiName = element(
      by.css('input[e2e="dashboard-add-kpi-name-input"]')
    );
    this._dateFieldSelect = element(
      by.css('[e2e="dashboard-add-kpi-date-column-select"]')
    );
    this._datePreSelect = element(
      by.css('[e2e="dashboard-add-kpi-date-preset-select"]')
    );
    this._aggregationSelect = element(
      by.css('[e2e="dashboard-add-kpi-aggregation-select"]')
    );
    this._secondaryAggregateByName = name =>
      element(
        by.xpath(`//*[@e2e="dashboard-add-kpi-secondary-aggregate-${name}"]`)
      );
    this._backgroundColorByName = name =>
      element(by.xpath(`//*[@e2e="dashboard-add-kpi-color-${name}"]`));
    this._applyKPIButton = element(
      by.css('button[e2e="dashboard-add-kpi-apply-button"]')
    );
    this._dateOrAggregationElement = name =>
      element(
        by.xpath(
          `//span[contains(text(),"${name}") and @class="mat-option-text"]`
        )
      );
    this._measure1Input = element(
      by.css('input[e2e="dashboard-add-kpi-bullet-measure1-input"]')
    );
    this._measure2Input = element(
      by.css('input[e2e="dashboard-add-kpi-bullet-measure2-input"]')
    );
    this._metricTargetInput = element(
      by.css('input[e2e="dashboard-add-kpi-bullet-target-input"]')
    );
    this._bandColor = name =>
      element(by.css(`[e2e="dashboard-add-kpi-bullet-color-${name}"]`));
  }

  addRemoveAnalysisById(analysesDetails) {
    analysesDetails.forEach(analysis => {
      commonFunctions.clickOnElement(
        this._addAnalysisById(analysis.analysisId)
      );
      expect(
        this._removeAnalysisById(analysis.analysisId).isDisplayed
      ).toBeTruthy();
    });
  }

  clickonAddAnalysisIdButton(id) {
    commonFunctions.clickOnElement(this._addAnalysisById(id));
  }

  clickOnKpiColumnByName(name) {
    commonFunctions.clickOnElement(this._kpiColumnByName(name));
  }

  fillKPINameDetails(text) {
    commonFunctions.fillInput(this._kpiName, text);
  }

  clickOnDateFieldSelect() {
    commonFunctions.clickOnElement(this._dateFieldSelect);
  }

  clickOnDatePreSelect() {
    commonFunctions.clickOnElement(this._datePreSelect);
  }

  clickOnAggregationSelect() {
    commonFunctions.clickOnElement(this._aggregationSelect);
  }

  clickOnDateOrAggregationElement(value) {
    commonFunctions.clickOnElement(this._dateOrAggregationElement(value));
  }

  addSecondryAggregations(kpiInfo) {
    kpiInfo.secondaryAggregations.forEach(secondaryAggregation => {
      if (
        secondaryAggregation.toLowerCase() !==
        kpiInfo.primaryAggregation.toLowerCase()
      ) {
        commonFunctions.clickOnElement(
          this._secondaryAggregateByName(secondaryAggregation)
        );
      }
    });
  }

  clickOnBackgroundColorByName(color) {
    commonFunctions.clickOnElement(this._backgroundColorByName(color));
  }

  clickOnApplyKPIButton() {
    commonFunctions.clickOnElement(this._applyKPIButton);
  }

  fillMeasure1Input(text) {
    commonFunctions.fillInput(this._measure1Input, text);
  }

  fillMeasure2Input(text) {
    commonFunctions.fillInput(this._measure2Input, text);
  }

  fillMetricTargetInput(text) {
    commonFunctions.fillInput(this._metricTargetInput, text);
  }

  clickOnBandColor(colorName) {
    commonFunctions.clickOnElement(this._bandColor(colorName));
  }
}

module.exports = SelectedItem;
