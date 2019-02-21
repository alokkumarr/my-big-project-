'use strict';

const commonFunctions = require('../utils/commonFunctions');

class SideNav {
  constructor() {
    this._accountSettingIcon = element(
      by.css(`[e2e='account-settings-menu-btn']`)
    );
    this._categoryByName = name =>
      element(
        by.xpath(
          `//span[text()="${name}"]/ancestor::mat-expansion-panel-header`
        )
      );
    this._subCategoryByName = name =>
      element(by.xpath(`//a[contains(text(),"${name}")]`));
    this._sideNavSection = element(by.xpath(`//mat-sidenav`));
    this._selectedCategoryTitle = name =>
      element(
        by.xpath(`//div[@e2e="category-title" and contains(text(),"${name}")]`)
      );
  }
  /**
   * @description Click on category if that is not expanded
   * @param {*} name
   */
  selectCategory(name) {
    const self = this;
    element(
      commonFunctions
        .hasClass(this._categoryByName(name), 'mat-expanded')
        .then(isPresent => {
          if (!isPresent) {
            commonFunctions.clickOnElement(self._categoryByName(name));
            browser.sleep(2000);
          }
        })
    );
  }
  /**
   * @description Click on sub category
   * @param {*} name
   */
  selectSubCategory(name) {
    commonFunctions.clickOnElement(self._subCategoryByName(name));
    browser.sleep(1000);
    commonFunctions.waitFor.elementToBeVisible(
      this._selectedCategoryTitle(name)
    );
  }
}

module.exports = SideNav;
