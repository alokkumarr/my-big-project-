'use strict';

const commonFunctions = require('./utils/commonFunctions');
const ConfirmationModel = require('./components/ConfirmationModel');

class ExecutePage extends ConfirmationModel {
  constructor() {
    super();
    this._actionMenuLink = element(by.css(`[e2e='actions-menu-toggle']`));
    this._analysisTitle = element(by.css(`span[e2e="analysis__title"]`));
    this._actionDetailsLink = element(
      by.css(`[e2e="actions-menu-selector-details"]`)
    );
    this._description = value =>
      element(by.xpath(`//p[contains(text(),"${value}")]`));
    this._drawer = element(
      by.xpath(`//div[contains(@class," mat-drawer-shown")]`)
    );
    this._delete = element(by.css(`[e2e='actions-menu-selector-delete']`));
    this._editLink = element(by.css(`[e2e="action-edit-btn"]`));
    this._forlAndEditLink = element(by.css(`[e2e="action-fork-btn"]`));
    this._executeButton = element(
      by.css(`button[e2e="actions-menu-selector-execute"]`)
    );
    this._selectedFilter = value =>
      element(by.css(`[e2e=filters-execute-"${value}"]`));
  }

  verifyTitle(title) {
    commonFunctions.waitFor.elementToBeVisible(this._analysisTitle);
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
    commonFunctions.waitFor.elementToBeVisible(this._drawer);
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
    browser.sleep(2000);
    commonFunctions.clickOnElement(this._delete);
  }

  getAnalysisId() {
    //get analysis id from current url
    browser.getCurrentUrl().then(url => {
      return commonFunctions.getAnalysisIdFromUrl(url);
    });
  }

  clickOnEditLink() {
    commonFunctions.clickOnElement(this._editLink);
    commonFunctions.waitFor.pageToBeReady(/edit/);
  }

  clickOnForkAndEditLink() {
    commonFunctions.clickOnElement(this._forlAndEditLink);
    commonFunctions.waitFor.pageToBeReady(/edit/);
  }

  clickOnExecuteButton() {
    commonFunctions.clickOnElement(this._executeButton);
  }

  /*
  @filters is array of object contains schema e.g.
  `[{
  "field":"Date",
  "displayedValue":"TW" // This week
  }]`
   */
  verifyAppliedFilter(filters) {
    filters.forEach(filter => {
      const value = `${filter.field}: ${filter.displayedValue}`;
      commonFunctions.waitFor.elementToBeVisible(this._selectedFilter(value));
    });
  }
}
module.exports = ExecutePage;
