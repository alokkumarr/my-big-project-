const commonFunctions = require('../../../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../../../../conf/protractor.conf');
const observePage = require('../../pages/observe/observePage.po');
const homePage = require('../../../javascript/pages/homePage.po.js');
let AnalysisHelper = require('../../../javascript/api/AnalysisHelper');
const utils = require('../../../javascript/helpers/utils');

class DashboardFunctions {

  goToObserve() {

    try {
      commonFunctions.waitFor.elementToBeVisible(homePage.observeLink);
      commonFunctions.waitFor.elementToBeClickable(homePage.observeLink);
      homePage.observeLink.click();
    } catch (e) {
      console.log(e);
    }

  }

  navigateToSubCategory(category, subCategory) {

    try {
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
    } catch (e) {
      console.log(e);
    }

  }

  addNewDashBoardFromExistingAnalysis(dashboardName, dashboardDescription, analysisCat, analysisSubCat, observeSubCat, analysesToAdd) {

    let dashboardId = null;

    try {

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
      dashboardId = _self.saveDashboard(dashboardName, dashboardDescription, observeSubCat);
    } catch (e) {
      console.log(e);
    } finally {
      return dashboardId;
    }

  }

  addAnalysesToDashboard(cat, subCat, analysesToAdd) {
    try {
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

        commonFunctions.waitFor.elementToBePresent(observePage.addAnalysisById(analysis.analysisId));
        commonFunctions.waitFor.elementToBeVisible(observePage.addAnalysisById(analysis.analysisId));
        commonFunctions.waitFor.elementToBeClickable(observePage.addAnalysisById(analysis.analysisId));
        observePage.addAnalysisById(analysis.analysisId).click();

        commonFunctions.waitFor.elementToBePresent(observePage.removeAnalysisById(analysis.analysisId));
        commonFunctions.waitFor.elementToBeVisible(observePage.removeAnalysisById(analysis.analysisId));
        expect(observePage.removeAnalysisById(analysis.analysisId).isDisplayed).toBeTruthy();
      });

      // Click on save button
      commonFunctions.waitFor.elementToBePresent(observePage.saveButton);
      commonFunctions.waitFor.elementToBeVisible(observePage.saveButton);
      commonFunctions.waitFor.elementToBeClickable(observePage.saveButton);
      expect(observePage.saveButton.isDisplayed).toBeTruthy();
      observePage.saveButton.click();

    } catch (e) {
      console.log(e);
    }
  }

  addAnalysisByApi(host, token, name, description, analysisType, subType) {

    try {
      let createdAnalysis = new AnalysisHelper().createNewAnalysis(host, token, name, description, analysisType, subType);
      let analysisId = createdAnalysis.contents.analyze[0].executionId.split('::')[0];

      let analysis = {
        analysisName: name,
        analysisId: analysisId
      };
      return analysis;
    } catch (e) {
      console.log(e);
    }

  }

  saveDashboard(name, description, subCat) {

    try {
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

    } catch (e) {
      console.log(e);
    } finally {
      commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardTitle(name));
      commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardTitle(name));
      expect(observePage.dashboard.dashboardTitle(name).isDisplayed).toBeTruthy();
      //get dashboard id from current url
      browser.getCurrentUrl().then(url => {
        let dashboardId =  url.split("=")[1];
        return dashboardId;
      });
    }
  }

  verifyDashboard(dashboardName, analysisName, del = true) {

    try {
      let _self = this;
      // Verify dashboard name
      commonFunctions.waitFor.elementToBePresent(observePage.dashboard.dashboardTitle(dashboardName));
      commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.dashboardTitle(dashboardName));
      expect(observePage.dashboard.dashboardTitle(dashboardName).isDisplayed).toBeTruthy();
      // Verify added analysis
      commonFunctions.waitFor.elementToBePresent(observePage.dashboard.addedAnalysisByName(analysisName));
      commonFunctions.waitFor.elementToBeVisible(observePage.dashboard.addedAnalysisByName(analysisName));
      expect(observePage.dashboard.addedAnalysisByName(analysisName).isDisplayed).toBeTruthy();
      // Verify dashboard actions
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
    } catch (e) {
      console.log(e);
    }finally {

    }

  }

  deleteDashboard(dashboardName) {

    try {
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
      commonFunctions.waitFor.elementToBeNotVisible(observePage.dashboard.dashboardTitle(dashboardName));
      expect(observePage.dashboard.dashboardTitle(dashboardName).isPresent()).toBeFalsy();

    } catch (e) {
      console.log(e);
    }
  }

}

module.exports = DashboardFunctions;
