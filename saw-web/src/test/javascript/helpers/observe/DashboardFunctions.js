const commonFunctions = require('../../../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../../../../conf/protractor.conf');
const observePage = require('../../pages/observe/observePage.po');
const homePage = require('../../../javascript/pages/homePage.po.js');
let AnalysisHelper = require('../../../javascript/api/AnalysisHelper');
const utils = require('../../../javascript/helpers/utils');

class DashboardFunctions {

  goToObserve() {
    commonFunctions.waitFor.elementToBeVisible(homePage.observeLink);
    commonFunctions.waitFor.elementToBeClickable(homePage.observeLink);
    homePage.observeLink.click();
  }

  navigateToSubCategory(category, subCategory) {

    homePage.mainMenuExpandBtn.click();
    browser.sleep(500);
    commonFunctions.waitFor.elementToBePresent(homePage.category(category));
    commonFunctions.waitFor.elementToBeVisible(homePage.category(category));
    //Navigate to Category/Sub-category, expand category
    commonFunctions.waitFor.elementToBeClickable(homePage.category(category));
    homePage.category(category).click();
    browser.sleep(500);
    commonFunctions.waitFor.elementToBeClickable(homePage.subCategory(subCategory));
    homePage.subCategory(subCategory).click();
    browser.sleep(1000);
  }

  addNewDashBoardFromExistingAnalysis(dashboardName, dashboardDescription, analysisCat, analysisSubCat, observeSubCat, analysesToAdd) {

    let _self = this;
    // Click on add dashboard button
    browser.sleep(500);
    commonFunctions.waitFor.elementToBePresent(observePage.addDashboardButton);
    commonFunctions.waitFor.elementToBeVisible(observePage.addDashboardButton);
    commonFunctions.waitFor.elementToBeClickable(observePage.addDashboardButton);
    expect(observePage.addDashboardButton.isDisplayed).toBeTruthy();
    observePage.addDashboardButton.click();
    browser.sleep(500);
    // Click on add widget button
    commonFunctions.waitFor.elementToBePresent(observePage.addWidgetButton);
    commonFunctions.waitFor.elementToBeVisible(observePage.addWidgetButton);
    commonFunctions.waitFor.elementToBeClickable(observePage.addWidgetButton);
    expect(observePage.addWidgetButton.isDisplayed).toBeTruthy();
    observePage.addWidgetButton.click();
    // Click on Existing Analysis link
    commonFunctions.waitFor.elementToBePresent(observePage.existingAnalysisLink);
    commonFunctions.waitFor.elementToBeVisible(observePage.existingAnalysisLink);
    commonFunctions.waitFor.elementToBeClickable(observePage.existingAnalysisLink);
    expect(observePage.existingAnalysisLink.isDisplayed).toBeTruthy();
    observePage.existingAnalysisLink.click();

    _self.addAnalysesToDashboard(analysisCat, analysisSubCat, analysesToAdd);
    _self.saveDashboard(dashboardName, dashboardDescription, observeSubCat)
  }

  addAnalysesToDashboard(cat, subCat, analysesToAdd) {

    // Click on category
    commonFunctions.waitFor.elementToBePresent(observePage.category(cat));
    commonFunctions.waitFor.elementToBeVisible(observePage.category(cat));
    commonFunctions.waitFor.elementToBeClickable(observePage.category(cat));
    expect(observePage.category(cat).isDisplayed).toBeTruthy();
    observePage.category(cat).click();

    // Click on subcategory
    commonFunctions.waitFor.elementToBePresent(observePage.subCategory(subCat));
    commonFunctions.waitFor.elementToBeVisible(observePage.subCategory(subCat));
    commonFunctions.waitFor.elementToBeClickable(observePage.subCategory(subCat));
    expect(observePage.subCategory(cat).isDisplayed).toBeTruthy();
    observePage.subCategory(subCat).click();

    // Add analyses
    analysesToAdd.forEach(function(analysis) {

      commonFunctions.waitFor.elementToBePresent(observePage.addAnalysisByName(analysis.analysisName));
      commonFunctions.waitFor.elementToBeVisible(observePage.addAnalysisByName(analysis.analysisName));
      commonFunctions.waitFor.elementToBeClickable(observePage.addAnalysisByName(analysis.analysisName));
      expect(observePage.addAnalysisByName(analysis.analysisName).isDisplayed).toBeTruthy();
      observePage.addAnalysisByName(analysis.analysisName).click();
      expect(utils.hasClass(observePage.addAnalysisByName(analysis.analysisName), 'widget-action-remove')).toBeTruthy();
    });

    // Click on save button
    commonFunctions.waitFor.elementToBePresent(observePage.saveButton);
    commonFunctions.waitFor.elementToBeVisible(observePage.saveButton);
    commonFunctions.waitFor.elementToBeClickable(observePage.saveButton);
    expect(observePage.saveButton.isDisplayed).toBeTruthy();
    observePage.saveButton.click();
  }

  addAnalysis(host, token, name, description, analysisType, subType) {

    let createdAnalysis = new AnalysisHelper().createNewAnalysis(host, token, name, description, analysisType, subType);
    let analysisId = createdAnalysis.contents.analyze[0].executionId.split('::')[0];

    let analysis = {
      analysisName:name,
      analysisId: analysisId
    }
    return analysis;
  }

  saveDashboard(name, description, subCat) {
    // Enter name
    commonFunctions.waitFor.elementToBePresent(observePage.dashboardName);
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboardName);
    observePage.dashboardName.clear();
    observePage.dashboardName.sendKeys(name);
    // Enter description
    commonFunctions.waitFor.elementToBePresent(observePage.dashboardDesc);
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboardDesc);
    observePage.dashboardDesc.clear();
    observePage.dashboardDesc.sendKeys(description);
    // Click on category
    commonFunctions.waitFor.elementToBePresent(observePage.categorySelect);
    commonFunctions.waitFor.elementToBeVisible(observePage.categorySelect);
    commonFunctions.waitFor.elementToBeClickable(observePage.categorySelect);
    observePage.categorySelect.click();
    browser.sleep(2000);
    // Click on subcategory
    commonFunctions.waitFor.elementToBePresent(observePage.subCategorySelect(subCat));
    commonFunctions.waitFor.elementToBeVisible(observePage.subCategorySelect(subCat));
    commonFunctions.waitFor.elementToBeClickable(observePage.subCategorySelect(subCat));
    observePage.subCategorySelect(subCat).click();

    commonFunctions.waitFor.elementToBePresent(observePage.saveDialogBtn);
    commonFunctions.waitFor.elementToBeVisible(observePage.saveDialogBtn);
    commonFunctions.waitFor.elementToBeClickable(observePage.saveDialogBtn);
    observePage.saveDialogBtn.click();
    expect(observePage.saveButton.isDisplayed).toBeTruthy();
  }

  verifyDashboard(dashboardName, del=true) {
    let _self = this;
    commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardTitle(dashboardName));
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardTitle(dashboardName));
    expect(observePage.dashboard.dashboardTitle(dashboardName).isDisplayed).toBeTruthy();

    commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardAction('Refresh'));
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardAction('Refresh'));
    expect(observePage.dashboard.dashboardAction('Refresh').isDisplayed).toBeTruthy();
    expect(observePage.dashboard.dashboardAction('Delete').isDisplayed).toBeTruthy();
    expect(observePage.dashboard.dashboardAction('Edit').isDisplayed).toBeTruthy();
    expect(observePage.dashboard.dashboardAction('Filter').isDisplayed).toBeTruthy();
    expect(browser.getCurrentUrl()).toContain('?dashboard');
    if (del) {
      _self.deleteDashboard(dashboardName);
    }
  }

  deleteDashboard(dashboardName) {

    // Delete created dashboard
    commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardAction('Delete'));
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardAction('Delete'));
    commonFunctions.waitFor.elementToBeClickable(observePage.dashboard.dashboardAction('Delete'));
    observePage.dashboard.dashboardAction('Delete').click();
    // Delete popup
    commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardConfirmDeleteButton);
    commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardConfirmDeleteButton);
    commonFunctions.waitFor.elementToBeClickable(observePage.dashboard.dashboardConfirmDeleteButton);
    observePage.dashboard.dashboardConfirmDeleteButton.click();
    browser.sleep(500);
    expect(observePage.dashboard.dashboardTitle(dashboardName).isPresent()).toBeFalsy();
  }

}
module.exports = DashboardFunctions;
