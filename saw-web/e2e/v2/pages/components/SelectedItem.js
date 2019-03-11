'use strict'

const commonFunctions = require('../utils/commonFunctions');
const SaveDashboardDialog = require('../components/SaveDashboardDialog');

class SelectedItem extends SaveDashboardDialog{
    constructor(){
        super();
        this._addAnalysisById= (id) =>  element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`));
        this._removeAnalysisById = (id) =>  element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`));
    }

    addRemoveAnalysisById(analysesDetails){
        analysesDetails.forEach( analysis => {
            commonFunctions.clickOnElement(this._addAnalysisById(analysis.analysisId));
            expect(this._removeAnalysisById(analysis.analysisId).isDisplayed).toBeTruthy();
        });
    }

    clickonAddAnalysisIdButton(id) {
        commonFunctions.clickOnElement(this._addAnalysisById(id));
    }
}

module.exports = SelectedItem;