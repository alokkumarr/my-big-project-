'use strict';
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
const CreateAnalysisModel = require('./components/CreateAnalysisModel');
const Utils = require('./utils/Utils');

class AnalyzePage extends CreateAnalysisModel {
  constructor() {
    super();
    this._addAnalysisButton = element(
      by.css(`[e2e="open-new-analysis-modal"]`)
    );
    this._listView = element(by.css('[e2e="analyze-list-view"]'));
    this._cardView = element(by.css('[e2e="analyze-card-view"]'));
    this._actionMenuButton = element(by.css(`[e2e='actions-menu-toggle']`));
    this._forkButton = element(by.css(`[e2e='action-fork-btn']`));
    this._executeButton = element(
      by.css(`[e2e="actions-menu-selector-execute"]`)
    );
    this._editButton = element(by.css(`[e2e="actions-menu-selector-edit"]`));
    this._publishButton = element(
      by.css(`[e2e="actions-menu-selector-publish"]`)
    );
    this._deleteButton = element(
      by.css(`[e2e="actions-menu-selector-delete"]`)
    );
    this._scheduleButton = element(
      by.xpath(
        `//*[@e2e="actions-menu-selector-publish" and contains(text(),"Schedule")]`
      )
    );
    this._actionMenuOptions = element(
      by.xpath('//div[contains(@class,"mat-menu-panel")]')
    );
    this._containerOverlay = element(by.css('[class="cdk-overlay-container"]'));
    this._firstCardTitle = element
      .all(by.css('a[e2e="analysis-name"]'))
      .first();
    this._editButtonOnExcutePage = element(by.css(`[e2e="action-edit-btn"]`));
    this._forkAndEditButton = element(by.css(`[e2e="action-fork-btn"]`));
    this._actionExportButton = element(
      by.css(`[e2e="actions-menu-selector-export`)
    );
    this._actionDetailsButton = element(
      by.css(`[e2e="actions-menu-selector-details"]`)
    );
    this._analysisTitleLink = name =>
      element(by.xpath(`//a[text()="${name}"]`));

    this._toastMessage = message =>
      element(
        by.xpath(
          `//*[@class="toast-message" and contains(text(),"${message}")]`
        )
      );
    this._labelNames = name => element(by.xpath(`//div[text()="${name}"]`));
    this._analyzeTypeSelector = elemenet(
      by.xpath(`//*[contains(@class,"select-form-field")]`)
    ); //[e2e="analyze-type-selector"]
    this._analysisType = name =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(),"${name}")]`
        )
      );
  }

  goToView(viewName) {
    if (viewName === 'card') {
      commonFunctions.waitFor.elementToBeVisible(this._cardView);
      element(
        Utils.hasClass(this._cardView, 'mat-radio-checked').then(isPresent => {
          if (!isPresent) {
            commonFunctions.waitFor.elementToBeClickable(this._cardView);
            this._cardView.click();
          }
        })
      );
    } else {
      commonFunctions.waitFor.elementToBeVisible(this._listView);
      element(
        Utils.hasClass(this._listView, 'mat-radio-checked').then(isPresent => {
          if (!isPresent) {
            commonFunctions.waitFor.elementToBeClickable(this._listView);
            this._listView.click();
          }
        })
      );
    }
    browser.sleep(1000);
  }

  clickOnAddAnalysisButton() {
    commonFunctions.waitFor.elementToBeClickable(this._addAnalysisButton);
    this._addAnalysisButton.click();
  }

  clickOnActionMenu() {
    const self = this;
    element(
      this._actionMenuButton.isPresent().then(function(isVisible) {
        if (isVisible) {
          commonFunctions.waitFor.elementToBeClickable(self._actionMenuButton);
          self._actionMenuButton.click();
        }
      })
    );
  }

  verifyElementPresent(myElement, isExist, message) {
    expect(myElement.isPresent()).toBe(isExist, message);
  }

  closeOpenedActionMenuFromCardView() {
    const self = this;
    element(
      this._actionMenuOptions.isPresent().then(function(isVisible) {
        if (isVisible) {
          commonFunctions.waitFor.elementToBeClickable(self._containerOverlay);
          self._containerOverlay.click();
          commonFunctions.waitFor.elementToBeNotVisible(
            self._actionMenuOptions
          );
        }
        expect(self._actionMenuOptions.isPresent()).toBe(false);
      })
    );
  }

  gotoAnalysisExecutePageFromCardView() {
    commonFunctions.waitFor.elementToBeClickable(this._firstCardTitle);
    this._firstCardTitle.click();
    const condition = ec.urlContains('/executed');
    browser
      .wait(() => condition, protractorConf.timeouts.pageResolveTimeout)
      .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));
  }

  clickOnAnalysisLink(name) {
    commonFunctions.waitFor.elementToBeClickable(this._analysisTitleLink(name));
    this._analysisTitleLink(name).click();
    commonFunctions.waitFor.pageToBeReady(/executed/);
  }

  verifyToastMessagePresent(message) {
    commonFunctions.waitFor.elementToBeVisible(this._toastMessage(message));
  }

  verifyAnalysisDeleted(name) {
    commonFunctions.waitFor.pageToBeReady(/analyze/);
    expect(this._analysisTitleLink(name).isPresent()).toBeFalsy();
  }

  verifyLabels(labels) {
    labels.forEach(label => {
      commonFunctions.waitFor.elementToBeVisible(this._labelNames(label));
    });
  }

  clickOnAnalysisTypeSelector() {
    commonFunctions.clickOnElement(this._analyzeTypeSelector);
  }

  verifyAnalysisTypeOptions(options) {
    options.forEach(option => {
      commonFunctions.waitFor.elementToBeVisible(this._analysisType(option));
    });
  }
}
module.exports = AnalyzePage;
