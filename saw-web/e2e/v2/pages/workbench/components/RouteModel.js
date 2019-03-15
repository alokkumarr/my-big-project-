'use strict';

const logger = require('../../../conf/logger')(__filename);
const commonFunctions = require('../../utils/commonFunctions');
const TestConnectivity = require('./TestConnectivity');

class RouteModel extends TestConnectivity {
  constructor() {
    super();
    this._routeNameInput = element(by.css(`[e2e="route-name-input"]`));
    this._routeSourceInput = element(
      by.css(`[e2e="route-source-location-input"]`)
    );
    this._routeFilePtrnInput = element(
      by.css(`[e2e="route-file-pattern-input"]`)
    );

    this._routeDestInput = element(by.css(`[e2e="route-dest-location-input"]`));

    this._routeExcludeInput = element(
      by.css(`[e2e="route-file-exclusions-input"]`)
    );

    this._routeBatchInput = element(by.css(`[e2e="route-batch-size-input"]`));

    this._routeDisableDuplicateInput = element(
      by.css(`[e2e="route-disable-duplicate-ch"]`)
    );

    this._routeDescInput = element(by.css(`[e2e="route-description-input"]`));
    this._routeCancelBtn = element(by.css(`[e2e="route-cancel-btn"]`));
    this._routePreviousBtn = element(by.css(`[e2e="route-schd-previous-btn"]`));
    this._routeNextBtn = element(by.css(`[e2e="route-next-btn"]`));
    this._schedule = name =>
      element(by.xpath(`//div[contains(text(),"${name}")]`));
    this._frequencyType = name =>
      element(by.xpath(`//*[@placeholder="${name}"]`));
    this._frequencyValue = numer =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(),"${numer}")][1]`
        )
      );
    this._startDate = element(
      by.xpath(`(//input[contains(@class,"date-input")])[1]`)
    );
    this._endDate = element(
      by.xpath(`(//input[contains(@class,"date-input")])[2]`)
    );
    this._increaseMinute = element(
      by.xpath(`(//span[@class="owl-dt-control-button-content"])[3]`)
    );
    this._set = element(by.xpath(`//span[text()="Set"]`));
    this._testConnBtn = element(by.css(`[e2e="route-schd-test-conn-btn"]`));
    this._createRouteBtn = element(by.css(`[e2e="route-schd-create-btn"]`));
    this._errorMessage = element(
      by.xpath(`//span[contains(@class,"errorTextMsg")]`)
    );
  }

  fillRouteName(name) {
    commonFunctions.fillInput(this._routeNameInput, name);
  }
  fillRouteSource(source) {
    commonFunctions.fillInput(this._routeSourceInput, source);
  }
  fillRouteFilePattern(filePattern) {
    commonFunctions.fillInput(this._routeFilePtrnInput, filePattern);
  }
  fillRouteDestination(dest) {
    commonFunctions.fillInput(this._routeDestInput, dest);
  }
  fillRouteBatchSize(batchSize) {
    commonFunctions.fillInput(this._routeBatchInput, batchSize);
  }
  fillRouteDescription(desc) {
    commonFunctions.fillInput(this._routeDescInput, desc);
  }
  clickOnRouteNextBtn() {
    commonFunctions.clickOnElement(this._routeNextBtn);
  }

  clickOnScheduleTab(name) {
    commonFunctions.waitFor.elementToBeVisible(this._routePreviousBtn);
    commonFunctions.clickOnElement(this._schedule(name));
    browser.sleep(2000);
  }

  clickOnFrequency(type, number) {
    // Wait is required because app is getting slow and not responding as expected very fast
    commonFunctions.clickOnElement(this._frequencyType(type));
    browser.sleep(1000);
    commonFunctions.clickOnElement(this._frequencyValue(number));
    browser.sleep(1000);
  }

  setScheduleStartDate() {
    commonFunctions.clickOnElement(this._startDate);
    browser.sleep(1000);
    commonFunctions.clickOnElement(this._increaseMinute);
    browser.sleep(1000);
    commonFunctions.clickOnElement(this._increaseMinute);
    commonFunctions.clickOnElement(this._set);
  }

  clickOnTestConnectivity() {
    commonFunctions.clickOnElement(this._testConnBtn);
    browser.sleep(5000);
  }

  clickOnCreateRouteBtn() {
    const _self = this;
    commonFunctions.clickOnElement(this._createRouteBtn);
    browser.sleep(200);
    element(
      this._errorMessage.isPresent().then(function(isVisible) {
        if (isVisible) {
          _self.setScheduleStartDate();
          commonFunctions.clickOnElement(this._createRouteBtn);
        }
      })
    );
  }
}
module.exports = RouteModel;
