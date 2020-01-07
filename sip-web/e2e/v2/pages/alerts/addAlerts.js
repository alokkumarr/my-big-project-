'use strict';

const commonFunctions = require('../utils/commonFunctions');
const ConfirmDeleteAlertDialog = require('./confirmDeleteAlertDialog');

class AddAlerts extends ConfirmDeleteAlertDialog {
  constructor() {
    super();
    this._alertName = element(by.css('input[e2e="alertName"]'));
    this._alertSeverity = element(by.css('mat-select[e2e="alertSeverity"]'));

    this._alertStatus = element(by.css('mat-select[e2e="alertStatus"]'));
    this._selectOptions = option =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(),"${option}")]`
        )
      );

    this._alertDescription = element(
      by.css('textarea[e2e="alertDescription"]')
    );

    this._toMetricSelectionButton = element(
      by.css('button[e2e="toMetricSelection"]')
    );

    this._selectDataPod = element(by.css('dx-select-box[e2e="datapodId"]'));
    this._dataPodOrMetric = datapod =>
      element(by.xpath(`//div[contains(text(),"${datapod}")]`));

    this._selectMetric = element(by.css('dx-select-box[e2e="metricName"]'));

    this._toAlertRulesButton = element(by.css('button[e2e="toAlertRules"]'));
    this._toAddStep = element(by.css('button[e2e="toAddStep"]'));

    this._toNameAlertButton = element(by.css('button[e2e="toNameAlert"]'));
    this._backtoMetricSelectionButton = element(
      by.css('button[e2e="backtoMetricSelection"]')
    );

    this._alertAggregation = element(
      by.css('mat-select[e2e="alertAggregation"]')
    );

    this._alertOperator = element(by.css('mat-select[e2e="alertOperator"]'));
    this._alertThresholdValue = element(
      by.css('input[e2e="alertThresholdValue"]')
    );

    this._addNewAlertButton = element(by.css('button[e2e="add-alert"]'));

    this._backtoAlertRulesButton = element(
      by.css('button[e2e="backtoAlertRules"]')
    );

    this._updateAlertButton = element(by.css('button[e2e="update-alert"]'));

    this._alertDetails = element(
      by.css('pre[e2e="add-container__body__stepper__validate"]')
    );
  }

  fillAlertName(text) {
    commonFunctions.fillInput(this._alertName, text);
  }

  clickOnAlertSeverity(text) {
    commonFunctions.clickOnElement(this._alertSeverity);
    commonFunctions.clickOnElement(this._selectOptions(text));
  }

  clickOnAlertStatus(text) {
    commonFunctions.clickOnElement(this._alertStatus);
    commonFunctions.clickOnElement(this._selectOptions(text));
  }

  clickOnAlertAggregationy(text) {
    commonFunctions.clickOnElement(this._alertAggregation);
    commonFunctions.clickOnElement(this._selectOptions(text));
  }

  clickOnAlertOperator(text) {
    commonFunctions.clickOnElement(this._alertOperator);
    commonFunctions.clickOnElement(this._selectOptions(text));
  }

  fillThresholdValue(val) {
    commonFunctions.fillInput(this._alertThresholdValue, val);
  }

  fillAlertDescription(text) {
    commonFunctions.fillInput(this._alertDescription, text);
  }

  clickOnToMetricSelectionButton() {
    commonFunctions.clickOnElement(this._toMetricSelectionButton);
  }

  clickOnToAlertRules() {
    commonFunctions.clickOnElement(this._toAlertRulesButton);
  }

  clickOnToNameAlert() {
    commonFunctions.clickOnElement(this._toNameAlertButton);
  }

  clickOnBacktoMetricSelectionButton() {
    commonFunctions.clickOnElement(this._backtoMetricSelectionButton);
  }

  clickOnToAddStep() {
    commonFunctions.clickOnElement(this._toAddStep);
  }

  clickOnUpdateAlertButton() {
    commonFunctions.clickOnElement(this._updateAlertButton);
  }

  clickOnSelectDataPod(text) {
    commonFunctions.clickOnElement(this._selectDataPod);
    commonFunctions.clickOnElement(this._dataPodOrMetric(text));
  }

  clickOnSelectMetric(text) {
    commonFunctions.clickOnElement(this._selectMetric);
    commonFunctions.clickOnElement(this._dataPodOrMetric(text));
  }

  clickOnAddNewAlertButton() {
    commonFunctions.clickOnElement(this._addNewAlertButton);
  }

  clickOnBacktoAlertRulesButton() {
    commonFunctions.clickOnElement(this._backtoAlertRulesButton);
  }
}

module.exports = AddAlerts;
