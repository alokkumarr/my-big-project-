'use strict';

const commonFunctions = require('./utils/commonFunctions');
const ConfirmationModel = require('./components/ConfirmationModel');
const Constants = require('../helpers/Constants');

class ExecutePage extends ConfirmationModel {
  constructor() {
    super();
    this._actionMenuLink = element(by.css(`[e2e='actions-menu-toggle']`));
    this._actionMenuContents = element(
      by.xpath(`//*[@class="mat-menu-content"]`)
    );
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
    this._editLink = element(by.css(`[e2e="action-edit-btn"]`));
    this._forkAndEditLink = element(by.css(`[e2e="action-fork-btn"]`));
    this._executeButton = element(
      by.css(`button[e2e="actions-menu-selector-execute"]`)
    );
    this._selectedFilter = value =>
      element(by.css(`[e2e="filters-execute-${value}"]`));
    this._reportColumnChooser = element(by.css(`[title="Column Chooser"]`));
    this._pivotData = element(
      by.xpath(`//pivot-grid[contains(@class,'executed-view-pivot')]`)
    );
    this._chartData = element(
      by.xpath(
        `//executed-chart-view[contains(@class,'executed-chart-analysis')]`
      )
    );
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
    commonFunctions.waitFor.elementToBeVisible(this._actionMenuContents);
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
    commonFunctions.clickOnElement(this._forkAndEditLink);
    commonFunctions.waitFor.pageToBeReady(/fork/);
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
  verifyAppliedFilter(filters, analysisType = null) {
    if (Constants.CHART === analysisType) {
      commonFunctions.waitFor.elementToBeVisible(this._chartData);
    } else if (Constants.PIVOT === analysisType) {
      commonFunctions.waitFor.elementToBeVisible(this._pivotData);
    } else if (
      Constants.REPORT === analysisType ||
      Constants.ES_REPORT === analysisType
    ) {
      commonFunctions.waitFor.elementToBeVisible(this._reportColumnChooser);
    }

    filters.forEach(filter => {
      const value = `${filter.field}: ${filter.displayedValue}`;
      browser.sleep(1500); // Some how this need to be added
      commonFunctions.waitFor.elementToBePresent(this._selectedFilter(value));
      commonFunctions.waitFor.elementToBeVisible(this._selectedFilter(value));
    });
  }
}
module.exports = ExecutePage;
