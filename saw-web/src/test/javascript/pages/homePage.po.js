const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const EC = protractor.ExpectedConditions;
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const analyzePage = require('../../javascript/pages/analyzePage.po');

module.exports = {
  mainMenuExpandBtn: element(by.css('mat-icon[e2e="main-menu-expand-btn"]')),
  mainMenuCollapseBtn: element(by.css('mat-icon[e2e="main-menu-expand-btn"]')),
  accountSettingsMenuBtn: element(by.css('mat-icon[e2e="account-settings-menu-btn"]')),
  adminMenuOption: element(by.css('a[e2e="account-settings-selector-admin"]')),
  changePasswordMenuOption: element(by.css('button[e2e="account-settings-selector-change-password"]')),
  cardViewButton: element(by.css('[e2e="analyze-card-view"]')),
  //In list view tag is "span". In card view tag is "a"
  savedAnalysis: analysisName => {
    return element(by.xpath(`//*[text() = "${analysisName}"]`));
  },
  expandedCategory: categoryName => {
    return element(by.xpath(`//span[contains(text(),'${categoryName}')]/../../../button`));
  },
  expandedCategoryUpdated: categoryName => {
    return element(by.xpath(`//span[contains(text(),"${categoryName}")]/parent::*/parent::*/parent::mat-expansion-panel-header[contains(@class,"mat-expanded")]`));
  },
  collapsedCategoryUpdated: categoryName => {
    return element(by.xpath(`//span[contains(text(),"${categoryName}")]/parent::*/parent::*/parent::mat-expansion-panel-header[not(contains(@class,"mat-expanded"))]`));
  },
  collapsedCategory: categoryName => {
    return element(by.xpath(`//ul[contains(@class,'is-collapsed')]/preceding-sibling::button/div/span[text()='${categoryName}']/../../../../../..`));
  },category: catName =>{
    return element(by.xpath(`//span[text()="${catName}"]/parent::mat-panel-title`));
  },
  subCategory: subCategoryName => {
    return element(by.xpath(`(//a[text()='${subCategoryName}'])[1]`));
  },
  navigateToSubCategory: (categoryName, subCategoryName, defaultCategory) => navigateToSubCategory(categoryName, subCategoryName, defaultCategory),
  navigateToSubCategoryUpdated: (categoryName, subCategoryName, defaultCategory) => navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory),
  createAnalysis: (metricName, analysisType) => createAnalysis(metricName, analysisType),
};

/*
 * Navigates to specific category where analysis creation should happen
 * @defaultCategory - category which should be collapsed before proceeding next
 * @categoryName - category to expand to reach subcategory
 * @subCategoryName - desirable category to expand
 */
const navigateToSubCategoryUpdated = (categoryName, subCategoryName, defaultCategory) => {
  //Collapse default category
  commonFunctions.waitFor.elementToBeClickable(module.exports.expandedCategoryUpdated(defaultCategory));
  module.exports.expandedCategoryUpdated(defaultCategory).click();
  commonFunctions.waitFor.elementToBePresent(module.exports.category(categoryName));
  commonFunctions.waitFor.elementToBeVisible(module.exports.category(categoryName));
  //Navigate to Category/Sub-category, expand category
  commonFunctions.waitFor.elementToBeClickable(module.exports.category(categoryName));
  module.exports.category(categoryName).click();

  const subCategory = module.exports.subCategory(subCategoryName);
  commonFunctions.waitFor.elementToBeClickable(subCategory);
  subCategory.click();
};
/*
 * Navigates to specific category where analysis creation should happen
 * @defaultCategory - category which should be collapsed before proceeding next
 * @categoryName - category to expand to reach subcategory
 * @subCategoryName - desirable category to expand
 */
const navigateToSubCategory = (categoryName, subCategoryName, defaultCategory) => {
  //Collapse default category
  commonFunctions.waitFor.elementToBeClickable(module.exports.category(defaultCategory));
  module.exports.category(defaultCategory).click();

  //Navigate to Category/Sub-category
 // const collapsedCategory = module.exports.category(categoryName);
 // const subCategory = module.exports.subCategory(subCategoryName);
  commonFunctions.waitFor.elementToBeClickable(module.exports.category(categoryName));
  module.exports.category(categoryName).click();
  commonFunctions.waitFor.elementToBeClickable(module.exports.subCategory(subCategoryName));
  module.exports.subCategory(subCategoryName).click();
};

const createAnalysis = (metricName, analysisType) => {
  commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.addAnalysisBtn);
  analyzePage.analysisElems.addAnalysisBtn.click();
  let count = 0;
  clickOnMetricRadioAndOnAnalysisType(metricName, analysisType, count);

  commonFunctions.waitFor.elementToBeEnabledAndVisible(analyzePage.newDialog.createBtn);
  commonFunctions.waitFor.elementToBeClickable(analyzePage.newDialog.createBtn);
  analyzePage.newDialog.createBtn.click();
};

/*
 * Click on two elements in sequence. If radio button wasn't selected, try again. Max 10 times
 * count should be accessible outside for recursion
 */
const clickOnMetricRadioAndOnAnalysisType = (metricName, analysisType, i) => {
  const newDialog = analyzePage.newDialog;
  const metricElement = newDialog.getMetricRadioButtonElementByName(metricName);
  const analysisTypeElement = newDialog.getAnalysisTypeButtonElementByType(analysisType);
  commonFunctions.waitFor.elementToBeClickable(metricElement);
  metricElement.click();

  // Check if metric selected
  browser.wait(EC.presenceOf(newDialog.getMetricSelectedRadioButtonElementByName(metricName)), 1000).then(
    function () {
      commonFunctions.waitFor.elementToBeClickable(analysisTypeElement);
      analysisTypeElement.click();
    }, function (err) {
      if (err) {
        console.log("AnalysisType is not clickable. Retrying click on Metric Radio Button. Tempts done: " + (i + 1));
        i++;
        browser.sleep(1000);
        if (i < protractorConf.timeouts.tempts) {
          clickOnMetricRadioAndOnAnalysisType(metricName, analysisType, i);
        } else {
          throw new Error("AnalysisType is not clickable after " +
            protractorConf.timeouts.tempts + " tries. Error: " + err);
        }
      }
    });
};
