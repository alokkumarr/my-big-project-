'use strict';

const commonFunctions = require('./utils/commonFunctions');
const AnalysisHelper = require('../helpers/api/AnalysisHelper');
const logger = require('../conf/logger')(__filename);


class ObservePage {
    constructor(){
        this._addDashboardButton = element(by.css(`[e2e='dashboard-new-dashboard-button']`));
        this._manualRefreshButton = element(by.css(`[e2e='dashboard-manual-refresh-button']`));
        this._deleteDashboardButton = element(by.css(`[e2e='dashboard-delete-dashboard-button']`));
        this._downloadDashboardButton = element(by.css(`[e2e='dashboard-download-dashboard-button']`));
        this._editDashboardButton = element(by.css(`[e2e='dashboard-edit-dashboard-button']`));
        this._openGlobalFilterButton = element(by.css(`[e2e='dashboard-open-global-filters-button']`));
        this._dashboardTitle = name => element(by.xpath(`//span[contains(text(),"${name}")]`));
        this._addedAnalysisByName = name => element(by.xpath(`//h1[text()="${name}"]`));
        this._dashboardAction = action =>  element(by.xpath(`//span[contains(text(),"${action}")]`));
    }

    getAddedAnalysisName(name) {
        return this._addedAnalysisByName(name)
    }

    clickOnAddDashboardButton() {
        commonFunctions.clickOnElement(this._addDashboardButton);
    }

    clickOnManualRefreshButton() {
        commonFunctions.clickOnElement(this._manualRefreshButton);
    }

    clickOnDeleteDashboardButton() {
        commonFunctions.clickOnElement(this._deleteDashboardButton);
    }

    clickOnDownloadDashboardButton() {
        commonFunctions.clickOnElement(this._downloadDashboardButton);
    }

    clickOnEditDashboardButton() {
        commonFunctions.clickOnElement(this._editDashboardButton);
    }

    clickOnOpenGlobalFilterButton() {
        commonFunctions.clickOnElement(this._openGlobalFilterButton);
    }

    verifyDashboardTitle(title) {
         expect(this._dashboardTitle(title).isDisplayed).toBeTruthy();
    }

    displayDashboardAction(text) {
        expect(this._dashboardAction(text).isDisplayed).toBeTruthy();
    }

    verifyDashboardTitleIsDeleted(dashboardName){
        commonFunctions.waitFor.elementToBeNotVisible(this._dashboardTitle(dashboardName));
        expect(this._dashboardTitle(dashboardName).isPresent()).toBeFalsy();  
    }

    verifyBrowserURLContainsText(text) {
        expect(browser.getCurrentUrl()).toContain(text);
    }

    addAnalysisByApi(
        host,
        token,
        name,
        description,
        analysisType,
        subType,
        filters = null
      ) {
        try {
          let createdAnalysis = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            name,
            description,
            analysisType,
            subType,
            filters
          );
          if (!createdAnalysis) {
            return null;
          }
          let analysisId = createdAnalysis.contents.analyze[0].executionId.split(
            '::'
          )[0];
    
          let analysis = {
            analysisName: name,
            analysisId: analysisId
          };
          return analysis;
        } catch (e) {
          logger.error(e);
        }
    }
}
module.exports = ObservePage;
