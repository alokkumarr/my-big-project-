'use strict';

const commonFunctions = require('./utils/commonFunctions');
const Utils = require('./utils/Utils');
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
    this._toastSuccess = element(by.css(`[class*='toast-success']`));

    this._aggregate = name => element(by.css(`[class*=' icon-${name}']`));
    this._previousVersion = element(
      by.xpath(`//span[text()='Previous Versions']`)
    );
    this._firstHistory = element(by.xpath(`(//tr)[2]`));
    this._executeButtonInDetailPage = element(
      by.xpath(`//span[contains(text(),'Execute')]/parent::button`)
    );
    this._gridViewIcon = element(by.css(`[mattooltip='Toggle to Grid']`));
    this._perPageSizeSection = element(by.css(`[class='dx-page-sizes']`));
    this._itemPerPageSizeSection = element(by.css(`[class='dx-page-sizes']`));
    this._totalPerPageOptions = element.all(
      by.xpath(
        `//div[@class='dx-page-sizes']/descendant::div[contains(@class,'dx-page-size')]`
      )
    );
    this._itemPerPageOptions = item =>
      element(
        by.xpath(
          `(//div[@class='dx-page-sizes']/descendant::div[contains(@class,'dx-page-size')])[${item}]`
        )
      );

    this._pagesSection = element(by.css(`[class='dx-pages']`));
    this._totalPages = element.all(
      by.xpath(
        `//div[@class='dx-pages']/descendant::div[contains(@class,'dx-page')]`
      )
    );
    this._paginationPage = number =>
      element(
        by.xpath(
          `(//div[@class='dx-pages']/descendant::div[contains(@class,'dx-page')])[${number}]`
        )
      );
    this._ScheduleButton = element(by.cssContainingText('button','Schedule'));
    this._previousversionTab = element(by.cssContainingText('span','Previous Versions'));
    this._scheduledInPreviousVersionTab = element(by.cssContainingText('td','scheduled'))
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

  aggregationVerification(aggregation) {
    commonFunctions.waitFor.elementToBeVisible(this._aggregate(aggregation));
  }

  clickOnToastSuccessMessage(designerLabel = null) {
    if (designerLabel === 'Distinct Count') {
      // Handle one special case where some issues
      commonFunctions.elementToBeClickableAndClickByMouseMove(
        this._toastSuccess
      );
    }
  }

  goToPreviousHistory() {
    commonFunctions.clickOnElement(this._previousVersion);
    commonFunctions.waitFor.elementToBeVisible(this._firstHistory);
    browser
      .actions()
      .mouseMove(this._firstHistory)
      .click()
      .perform();
  }

  clickOnGridViewIcon() {
    commonFunctions.clickOnElement(this._gridViewIcon);
  }

  verifyItemPerPage() {
    commonFunctions.waitFor.elementToBeVisible(this._perPageSizeSection);
    commonFunctions.scrollIntoView(this._perPageSizeSection);
    let _self = this;
    this._totalPerPageOptions.count().then(total => {
      let pos = 1; // 1 is already selected so it should be clickable

      (function loop() {
        if (pos <= total) {
          _self._itemPerPageOptions(pos).click();
          browser.sleep(2000); // Need to add this else getting stale element exception
          expect(_self._pagesSection.isDisplayed()).toBeTruthy();
          pos++;
          loop();
        }
      })();
    });
  }

  verifyPagination() {
    commonFunctions.waitFor.elementToBeVisible(this._pagesSection);
    commonFunctions.scrollIntoView(this._pagesSection);
    let _self = this;
    this._totalPages.count().then(total => {
      let pos = 1; // 1 is already selected so it should be clickable

      (function loop() {
        if (pos <= total) {
          _self._paginationPage(pos).click();
          browser.sleep(2000); // Need to add this else getting stale element exception
          expect(_self._perPageSizeSection.isDisplayed()).toBeTruthy();
          pos++;
          loop();
        }
      })();
    });
  }
  /*Method to click on schedule option*/
  clickOnSchedule() {
    commonFunctions.clickOnElement(this._ScheduleButton);
    browser.sleep(2000); //Need to Add Because Schedule Popup Should load
  }
  /*Method to click on previous version tab*/
  clickOnPreviousVersionTab() {
    commonFunctions.clickOnElement(this._previousversionTab);
  }
  /*Method to verify report is scheduled*/
  verifyScheduledInPreviousVersion() {
    commonFunctions.waitFor.elementToBeVisible(this._scheduledInPreviousVersionTab);
    element(
      this._scheduledInPreviousVersionTab.getText().then(value => {
        if (value) {
          expect(value.trim().toUpperCase()).toEqual('SCHEDULED');
        } else {
          expect(false).toBe(
            true,
            'Scheduled, it was expected to be present but found false'
          );
        }
      })
    );
  }

}
module.exports = ExecutePage;
