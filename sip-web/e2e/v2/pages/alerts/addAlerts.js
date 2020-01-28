'use strict';

const commonFunctions = require('../utils/commonFunctions');
const ConfirmDeleteAlertDialog = require('./confirmDeleteAlertDialog');

class AddAlerts extends ConfirmDeleteAlertDialog {
  constructor() {
    super();
    this._alertName = element(by.css('[e2e="alertRuleName"]'));
    this._alertSeverity = element(by.css('[e2e="alertSeverity"]'));

    this._alertStatus = element(by.css('[e2e="alertStatus"]'));

    this._alertDescription = element(by.css('[e2e="alertRuleDescription"]'));

    this._toMetricSelectionButton = element(
      by.css('[e2e="toMetricSelection"]')
    );

    this._selectDataPod = element(by.css('[e2e="datapodId"]'));

    this._selectMetric = element(by.css('[e2e="metricsColumn"]'));

    this._toAlertRulesButton = element(by.css('[e2e="toAlertRules"]'));
    this._toAddStep = element(by.css('[e2e="toAddStep"]'));

    this._toNameAlertButton = element(by.css('[e2e="toNameAlert"]'));
    this._backtoMetricSelectionButton = element(
      by.css('[e2e="backtoMetricSelection"]')
    );

    this._alertAggregation = element(by.css('[e2e="alertAggregation"]'));

    this._alertOperator = element(by.css('[e2e="alertOperator"]'));
    this._alertThresholdValue = element(by.css('[e2e="alertThresholdValue"]'));

    this._addNewAlertButton = element(by.css('[e2e="add-alert"]'));

    this._backtoAlertRulesButton = element(by.css('[e2e="backtoAlertRules"]'));

    this._updateAlertButton = element(by.css('[e2e="update-alert"]'));

    this._alertDetails = element(
      by.css('[e2e="add-container__body__stepper__validate"]')
    );
    this._notification = element(by.css(`[e2e="notification"]`));
    this._notificationMethod = element(by.xpath(`//span[text()='email']`));
    this._notificationValue = element(by.css(`[e2e='email-list-input']`));
    this._overlay = element(
      by.xpath(`//*[contains(@class,'cdk-overlay-backdrop-showing')]`)
    );
    this._monitoringType = element(by.css(`[e2e='monitoringType']`));
    this._selectValueFromDropDown = value =>
      element(by.css(`[e2e='${value}']`));

    this._selectValueFromSearchAbleDropDown = value =>
      element(by.xpath(`//*[contains(text(),'${value}')]`));

    this._aggregationType = element(by.css(`[e2e="aggregationType"]`));
    this._alertOperator = element(by.css(`[e2e='alertOperator']`));
    this._lookbackColumn = element(by.css(`[e2e='lookbackColumn']`));
    this._lookbackPeriodValue = element(by.css(`[e2e='lookbackPeriodValue']`));
    this._lookbackPeriodType = element(by.css(`[e2e='lookbackPeriodType']`));
    this._attributeName = element(by.css(`[e2e='attributeName']`));
    this._attributeValue = element(by.css(`[e2e='attributeValue']`));
  }

  selectLokbackColumn(value) {
    commonFunctions.clickOnElement(this._lookbackColumn);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  fillLookBackPeriod(value) {
    commonFunctions.fillInput(this._lookbackPeriodValue, value);
  }

  selectLookbackPeriodType(value) {
    commonFunctions.clickOnElement(this._lookbackPeriodType);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  selectAttributeName(value) {
    commonFunctions.clickOnElement(this._attributeName);
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.clickOnElement(
      this._selectValueFromSearchAbleDropDown(value)
    );
    commonFunctions.waitForProgressBarToComplete();
  }

  selectAttributeValue(value) {
    commonFunctions.clickOnElement(this._attributeValue);
    commonFunctions.clickOnElement(
      this._selectValueFromSearchAbleDropDown(value)
    );
  }

  selectMonitoringType(value) {
    commonFunctions.clickOnElement(this._monitoringType);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  selectAggregationType(value) {
    commonFunctions.clickOnElement(this._aggregationType);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  selectAlertOperator(value) {
    commonFunctions.clickOnElement(this._alertOperator);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  clickOnNotification() {
    commonFunctions.clickOnElement(this._notification);
  }

  clickOnNotificationMethod(value) {
    commonFunctions.clickOnElement(this._selectValueFromDropDown(value));
  }

  FillNotificationValue(text) {
    this._overlay.click();
    commonFunctions.fillInput(this._notificationValue, text);
  }

  fillAlertName(text) {
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.fillInput(this._alertName, text);
  }

  clickOnAlertSeverity(text) {
    commonFunctions.clickOnElement(this._alertSeverity);
    commonFunctions.clickOnElement(
      this._selectValueFromDropDown(text.toUpperCase())
    );
  }

  clickOnAlertStatus(text) {
    commonFunctions.clickOnElement(this._alertStatus);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(text));
  }

  clickOnAlertAggregationy(text) {
    commonFunctions.clickOnElement(this._alertAggregation);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(text));
  }

  clickOnAlertOperator(text) {
    commonFunctions.clickOnElement(this._alertOperator);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(text));
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
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.clickOnElement(this._selectDataPod);
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.clickOnElement(this._selectValueFromDropDown(text));
  }

  clickOnSelectMetric(text) {
    commonFunctions.waitForProgressBarToComplete();
    commonFunctions.clickOnElement(this._selectMetric);
    commonFunctions.clickOnElement(this._selectValueFromDropDown(text));
  }

  clickOnAddNewAlertButton() {
    commonFunctions.clickOnElement(this._addNewAlertButton);
  }

  clickOnBacktoAlertRulesButton() {
    commonFunctions.clickOnElement(this._backtoAlertRulesButton);
  }
}

module.exports = AddAlerts;
