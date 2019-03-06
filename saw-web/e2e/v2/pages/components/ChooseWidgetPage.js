
'use strict';

const commonFunctions = require('../utils/commonFunctions');


class ChooseWidgetPage {
    constructor(){
        this._existingAnalysisLink= element(by.css('button[e2e="dashboard-add-widget-type-analysis"]'));
        this._category = (name) =>  element(by.xpath(`//span[contains(text(),"${name}")]`));
        this._subCategory = (name) => element(by.xpath(`//span[contains(text(),"${name}")]`));
        this._addAnalysisById= (id) =>  element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`));
        this._removeAnalysisById = (id) =>  element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`));
    }


    addRemoveAnalysisById(analysesDetails) 
    {
        analysesDetails.forEach( analysis => {
            commonFunctions.clickOnElement(this._addAnalysisById(analysis.analysisId));
            expect(this._removeAnalysisById(analysis.analysisId).isDisplayed).toBeTruthy();
        });
    }
    
    getDashboardId(){
        browser.getCurrentUrl().then(url => {
            return url.split('=')[1];
        });
    }

    clickOnExistingAnalysisLink() {
        commonFunctions.clickOnElement(this._existingAnalysisLink);
    }

    clickOnCategory(categoryName) {
        commonFunctions.clickOnElement(this._category(categoryName));
    }

    clickOnSubCategory(subCategoryName) {
        commonFunctions.clickOnElement(this._subCategory(subCategoryName));
    }

    clickonAddAnalysisIdButton(id) {
        commonFunctions.clickOnElement(this._addAnalysisById(id));
    }

    getRemoveAnalysisByIdElement(id) {
        return this._removeAnalysisById(id);
    }
}

module.exports = ChooseWidgetPage;