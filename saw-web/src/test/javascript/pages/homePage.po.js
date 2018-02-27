const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');

module.exports = {
  accountSettingsMenuBtn: element(by.css('button[e2e="account-settings-menu-btn"]')),
  adminMenuOption: element(by.css('a[e2e="account-settings-selector-admin"]')),
  changePasswordMenuOption: element(by.css('button[e2e="account-settings-selector-change-password"]')),
  cardViewButton: element(by.css('[ng-value="$ctrl.CARD_VIEW"]')),
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
  navigateToSubCategory: (categoryName, subCategoryName) => navigateToSubCategory,
  createAnalysis : (metricName, analysisType) => createAnalysis(metricName, analysisType)
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


const createAnalysis = (metricName, analysisType) => {
  analyzePage.analysisElems.addAnalysisBtn.click();
  const newAnalysisDialog = analyzePage.newAnalysisDialog;
  newAnalysisDialog.getMetric(metricName).click();
  newAnalysisDialog.getMethod(analysisType).click();
  newAnalysisDialog.createBtn.click();
};
