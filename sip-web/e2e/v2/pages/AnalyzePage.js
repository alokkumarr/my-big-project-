'use strict';
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
const CreateAnalysisModel = require('./components/CreateAnalysisModel');
const Utils = require('./utils/Utils');
const log = require('../conf/logger')(__filename);

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
    this._analyzeExecuteButton = element(
      by.css(`button[e2e="actions-menu-selector-execute"]`)
    );
    this._editButton = element(by.css(`[e2e="actions-menu-selector-edit"]`));
    this._publishButton = element(
      by.css(`[e2e="actions-menu-selector-publish"]`)
    );
    this._deleteButton = element(
      by.css(`[e2e="actions-menu-selector-delete"]`)
    );
    this._scheduleButton = element(
      by.css(`[e2e="actions-menu-selector-schedule"]`)
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
    this._analyzeTypeSelector = element(
      by.xpath(`//*[contains(@class,"select-form-field")]`)
    ); //[e2e="analyze-type-selector"]
    this._analysisTypeDsiplay = name =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(),"${name}")]`
        )
      );
    this._actionLinkByAnalysisName = name =>
      element(
        by.xpath(
          `(//*[text()="${name}"]/following::*[@e2e="actions-menu-toggle"])[1]`
        )
      );

    this._forkButtonByAnalysis = name =>
      element(
        by.xpath(
          `//a[contains(text(),'${name}')]/following::button[@e2e='action-fork-btn']`
        )
      );
    this._scheduledTimingsInListView = reportName =>
      element(by.xpath(`//a[text()='${reportName}']/following::td[2]`));
    this._scheduledTimingsInCardView = reportName =>
      element(
        by.xpath(
          `//a[text()='${reportName}']/following::mat-card-subtitle/span[2]`
        )
      );
    this._analysisTypeDropdown = element(by.css(`[role="listbox"]`));
    this._verifyAnalysisTypeListView = analysisName =>
      element(by.xpath(`//a[text()='${analysisName}']/following::td[3]`));
    this._verifyAnalysisTypeCardView = analysisName =>
      element(by.xpath(`//a[text()='${analysisName}']/preceding::mat-chip`));
    this._createAnalysisModalTitle = element(
      by.xpath(` //h2[contains(text(),'Create New Analysis')]`)
    );
  }

  goToView(viewName) {
    if (viewName === 'card') {
      commonFunctions.waitFor.elementToBeVisible(this._cardView);
      element(
        Utils.hasClass(this._cardView, 'mat-radio-checked').then(isPresent => {
          if (!isPresent) {
            commonFunctions.clickOnElement(this._cardView);
          }
        })
      );
    } else {
      commonFunctions.waitFor.elementToBeVisible(this._listView);
      element(
        Utils.hasClass(this._listView, 'mat-radio-checked').then(isPresent => {
          if (!isPresent) {
            commonFunctions.clickOnElement(this._listView);
          }
        })
      );
    }
    browser.sleep(1000);
  }

  clickOnAddAnalysisButton() {
    commonFunctions.clickOnElement(this._addAnalysisButton);
    browser.sleep(2000);
    let self = this;
    // model is not displayed then again click
    element(
      this._createAnalysisModalTitle.isPresent().then(function (isVisible) {
        if (!isVisible) {
          log.info('again clickig on the add anlysis button');
          commonFunctions.clickOnElement(self._addAnalysisButton);
        }
      })
    );
  }
  clickOnActionMenu() {
    const self = this;
    element(
      this._actionMenuButton.isPresent().then(function (isVisible) {
        if (isVisible) {
          commonFunctions.clickOnElement(self._actionMenuButton);
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
      this._actionMenuOptions.isPresent().then(function (isVisible) {
        if (isVisible) {
          commonFunctions.clickOnElement(self._containerOverlay);
          commonFunctions.waitFor.elementToBeNotVisible(
            self._actionMenuOptions
          );
        }
        expect(self._actionMenuOptions.isPresent()).toBe(false);
      })
    );
  }

  gotoAnalysisExecutePageFromCardView() {
    commonFunctions.clickOnElement(this._firstCardTitle);
    const condition = ec.urlContains('/executed');
    browser
      .wait(() => condition, protractorConf.timeouts.pageResolveTimeout)
      .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));
  }

  clickOnAnalysisLink(name) {
    commonFunctions.waitFor.elementToBeVisible(this._analysisTitleLink(name));
    commonFunctions.clickOnElement(this._analysisTitleLink(name));
    commonFunctions.waitFor.pageToBeReady(/executed/);
  }

  verifyToastMessagePresent(message) {
    this._toastMessage(message)
      .isDisplayed()
      .then(
        () => {
          this._toastMessage(message)
            .getText()
            .then(value => {
              if (value) {
                expect(value.trim()).toEqual(message);
                commonFunctions.clickOnElement(this._toastMessage(message));
                browser.sleep(2000); // Need to wait else logout button will not be visible
              }
            });
        },
        () => {
          log.debug('Toast Message did not display');
        }
      );
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
      commonFunctions.waitFor.elementToBeVisible(
        this._analysisTypeDsiplay(option)
      );
    });
  }

  clickOnExecuteButtonAnalyzePage() {
    browser.sleep(2000);
    commonFunctions.clickOnElement(this._analyzeExecuteButton);
  }

  clickOnActionLinkByAnalysisName(name) {
    commonFunctions.clickOnElement(this._actionLinkByAnalysisName(name));
  }

  clickOnForkButton(name) {
    commonFunctions.clickOnElement(this._forkButtonByAnalysis(name));
  }

  /*Method to verify report is scheduled*/
  verifyScheduledTimingsInCardView(reportName, scheduledTime) {
    commonFunctions.waitFor.elementToBeVisible(
      this._scheduledTimingsInCardView(reportName)
    );
    element(
      this._scheduledTimingsInCardView(reportName)
        .getText()
        .then(value => {
          if (value) {
            expect(value.trim()).toEqual(scheduledTime);
          } else {
            expect(false).toBe(true, 'Scheduled Timings did not match');
          }
        })
    );
  }

  /*Method to verify report is scheduled*/
  verifyScheduledTimingsInListView(reportName, scheduledTime) {
    commonFunctions.waitFor.elementToBeVisible(
      this._scheduledTimingsInListView(reportName)
    );
    element(
      this._scheduledTimingsInListView(reportName)
        .getText()
        .then(value => {
          if (value) {
            expect(value.trim()).toEqual(scheduledTime);
          } else {
            expect(false).toBe(true, 'Scheduled Timings did not match');
          }
        })
    );
  }

  goToDesignerPage(view, analysisType, dataPods) {
    this.goToView(view);
    this.clickOnAddAnalysisButton();
    this.clickOnAnalysisType(analysisType);
    this.clickOnNextButton();
    this.clickOnDataPods(dataPods);
    this.clickOnCreateButton();
  }

  goToViewAndSelectAnalysis(viewName, analysisName) {
    if (viewName === 'details') {
      this.clickOnAnalysisLink(analysisName);
    } else {
      this.goToView(viewName);
      this.clickOnActionLinkByAnalysisName(analysisName);
    }
  }

  clickOnAnalysisTypeDropDown() {
    commonFunctions.clickOnElement(this._analysisTypeDropdown);
  }
  selectAnalysisType(analysisType) {
    commonFunctions.clickOnElement(this._analysisTypeDsiplay(analysisType));
  }

  goToViewAndVerifyAnalysis(analysisName, reportType) {
    this.goToView('card');
    expect(this._verifyAnalysisTypeCardView(analysisName).getText()).toEqual(
      reportType.toUpperCase()
    );
    this.goToView('list');
    expect(this._verifyAnalysisTypeListView(analysisName).getText()).toEqual(
      reportType.toUpperCase()
    );
  }

  chooseAnalysisTypeAndVerify(analysisType, chartName, pivotName, reportName) {
    this.clickOnAnalysisTypeDropDown();
    this.selectAnalysisType(analysisType);
    switch (analysisType) {
      case 'Chart':
        this.goToViewAndVerifyAnalysis(chartName, analysisType.toUpperCase());
        break;
      case 'Pivot':
        this.goToViewAndVerifyAnalysis(pivotName, analysisType.toUpperCase());
        break;
      case 'Report':
        this.goToViewAndVerifyAnalysis(reportName, analysisType.toUpperCase());
        break;
    }
  }
}
module.exports = AnalyzePage;
