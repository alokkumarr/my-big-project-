const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const EC = protractor.ExpectedConditions;
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const analyzePage = require('../../javascript/pages/analyzePage.po');

module.exports = {
  mainMenuExpandBtn: element(by.css('button[e2e="main-menu-expand-btn"]')),
  mainMenuCollapseBtn: element(by.css('button[e2e="main-menu-collapse-btn"]')),
  accountSettingsMenuBtn: element(by.css('button[e2e="account-settings-menu-btn"]')),
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
  collapsedCategory: categoryName => {
    return element(by.xpath(`//ul[contains(@class,'is-collapsed')]/preceding-sibling::button/div/span[text()='${categoryName}']/../../../../../..`));
  },
  subCategory: subCategoryName => {
    return element(by.xpath(`(//span[text()='${subCategoryName}'])[1]`));
  },
  navigateToSubCategory: (categoryName, subCategoryName, defaultCategory) => navigateToSubCategory(categoryName, subCategoryName, defaultCategory),
  createAnalysis: (metricName, analysisType) => createAnalysis(metricName, analysisType),
};

/*
 * Navigates to specific category where analysis creation should happen
 * @defaultCategory - category which should be collapsed before proceeding next
 * @categoryName - category to expand to reach subcategory
 * @subCategoryName - desirable category to expand
 */
const navigateToSubCategory = (categoryName, subCategoryName, defaultCategory) => {
  //Collapse default category
  commonFunctions.waitFor.elementToBeClickableAndClick(module.exports.expandedCategory(defaultCategory));

  //Navigate to Category/Sub-category
  const collapsedCategory = module.exports.collapsedCategory(categoryName);
  const subCategory = module.exports.subCategory(subCategoryName);
  commonFunctions.waitFor.elementToBeClickableAndClick(collapsedCategory);
  commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);
};

const createAnalysis = (metricName, analysisType) => {
  commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.addAnalysisBtn);
  browser.sleep(1000);
  let count = 0;
  clickOnMetricRadioAndOnAnalysisType(metricName, analysisType, count);

  commonFunctions.waitFor.elementToBeEnabledAndVisible(analyzePage.newDialog.createBtn);
  commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.newDialog.createBtn);
};

/*
 * Click on two elements in sequence. If radio button wasn't selected, try again. Max 10 times
 * count should be accessible outside for recursion
 */
const clickOnMetricRadioAndOnAnalysisType = (metricName, analysisType, i) => {
  const newDialog = analyzePage.newDialog;
  const metricElement = newDialog.getMetricRadioButtonElementByName(metricName);
  const analysisTypeElement = newDialog.getAnalysisTypeButtonElementByType(analysisType);

  commonFunctions.waitFor.elementToBeClickableAndClick(metricElement);

  // Check if metric selected
  browser.wait(EC.presenceOf(newDialog.getMetricSelectedRadioButtonElementByName(metricName)), 1000).then(
    function () {
      commonFunctions.waitFor.elementToBeClickableAndClick(analysisTypeElement);
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
