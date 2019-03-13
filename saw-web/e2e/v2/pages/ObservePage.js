'use strict';

const commonFunctions = require('./utils/commonFunctions');
const DashboardHeader = require('../pages/DashboardHeader');

class ObservePage extends DashboardHeader{
    constructor(){
        super();
        this._dashboardTitle = name => element(by.xpath(`//span[contains(text(),"${name}")]`));
        this._addedAnalysisByName = name => element(by.xpath(`//h1[text()="${name}"]`));
    }

    verifyDashboardTitle(title) {
        expect(this._dashboardTitle(title).isDisplayed).toBeTruthy();
    }

    verifyDashboardTitleIsDeleted(dashboardName){
        commonFunctions.waitFor.elementToBeNotVisible(this._dashboardTitle(dashboardName));
        expect(this._dashboardTitle(dashboardName).isPresent()).toBeFalsy();  
    }

    verifyBrowserURLContainsText(text) {
        expect(browser.getCurrentUrl()).toContain(text);
    }

    verifyAddedAnalysisName(name) {
        expect(this._addedAnalysisByName(name).isDisplayed).toBeTruthy();
    }
}
module.exports = ObservePage;