'use strict';
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
const CreateAnalysisModel = require('./components/CreateAnalysisModel');

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
  }

  goToView(viewName) {
    if (viewName === 'card') {
      commonFunctions.waitFor.elementToBeClickable(this._cardView);
      this._cardView.click();
    } else {
      commonFunctions.waitFor.elementToBeClickable(this._listView);
      this._listView.click();
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
    element(
      myElement.isPresent().then(function(isVisible) {
        expect(isVisible).toBe(isExist, message);
      })
    );
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
}
module.exports = AnalyzePage;
