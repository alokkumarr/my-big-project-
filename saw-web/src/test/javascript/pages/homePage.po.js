const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');

module.exports = {
  accountSettingsMenuBtn: element(by.css('button[e2e="account-settings-menu-btn"]')),
  adminMenuOption: element(by.css('a[e2e="account-settings-selector-admin"]')),
  changePasswordMenuOption: element(by.css('button[e2e="account-settings-selector-change-password"]')),
  //In list view tag is "span". In card view tag is "a"
  savedAnalysis: analysisName => {
    return element(by.xpath(`//*[text() = "${analysisName}"]`))
  },
  expandedCategory: categoryName => {
    return element(by.xpath(`//span[contains(text(),'${categoryName}')]/../../../button`));
  },
  collapsedCategory: categoryName => {
    return element(by.xpath(`//ul[contains(@class,'is-collapsed')]/preceding-sibling::button/div/span[text()='${categoryName}']/../../../../../..`));
  },
  subCategory: subCategoryName => {
    return element(by.xpath(`(//span[text()='${subCategoryName}'])[1]`));
  },
  navigateToSubCategory: (categoryName, subCategoryName) => navigateToSubCategory
};

// Navigates to specific category where analysis creation should happen
const navigateToSubCategory = (categoryName, subCategoryName) => {
  //Collapse default category
  homePage.expandedCategory(defaultCategory).click();

  //Navigate to Category/Sub-category
  const collapsedCategory = homePage.collapsedCategory(categoryName);
  const subCategory = homePage.subCategory(subCategoryName);
  commonFunctions.waitFor.elementToBeClickableAndClick(collapsedCategory);
  commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);
};
