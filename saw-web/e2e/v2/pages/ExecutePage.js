'use strict';
const commonFunctions = require('./utils/commonFunctions');
const protractorConf = require('../conf/protractor.conf');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;

const ConfirmationModel = require('./components/ConfirmationModel');

class ExecutePage extends ConfirmationModel {
  constructor() {
    super();
    this._actionMenuLink = element(by.css(`[e2e='actions-menu-toggle']`));
    this._analysisTitle = element(by.css(`[class="analysis__title"]`));
    this._actionDetailsLink = element(
      by.css(`[e2e="actions-menu-selector-details"]`)
    );
    this._description = value =>
      element(by.xpath(`//p[contains(text(),"${value}")]`));
    this._drawer = element(
      by.xpath(`//div[contains(@class," mat-drawer-shown")]`)
    );
    this._delete = element(by.css(`[e2e='actions-menu-selector-delete']`));
  }

  verifyTitle(title) {
    element(
      this._analysisTitle.getText().then(value => {
        if (value) {
          expect(value.trim()).toEqual(title.trim());
        } else {
          expect(false).toBe(
            true,
            'Ananlysis title cannot be , it was expected to be present but found false'
          );
        }
      })
    );
  }

  clickOnActionLink() {
    commonFunctions.clickOnElement(this._actionMenuLink);
  }

  clickOnDetails() {
    commonFunctions.clickOnElement(this._actionDetailsLink);
  }

  verifyDescription(description) {
    commonFunctions.waitFor.elementToBeVisible(this._description(description));
  }

  closeActionMenu() {
    element(
      this._drawer.isPresent().then(isPresent => {
        if (isPresent) {
          expect(isPresent).toBeTruthy();
          this._drawer.click();
          commonFunctions.waitFor.elementToBeNotVisible(this._drawer);
        }
      })
    );
  }

  clickOnDelete() {
    commonFunctions.clickOnElement(this._delete);
  }

  getAnalysisId() {
    //get analysis id from current url
    browser.getCurrentUrl().then(url => {
      return commonFunctions.getAnalysisIdFromUrl(url);
    });
  }
}
module.exports = ExecutePage;
