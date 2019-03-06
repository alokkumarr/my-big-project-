'use strict';

const commonFunctions = require('../pages/utils/commonFunctions');
const ChooseWidgetPage = require('../pages/components/ChooseWidgetPage');

class CreateNewDashboard extends ChooseWidgetPage {
    constructor() {
        super();
        this._addWidgetButton = element(by.css(`[e2e='dashboard-add-widget-button']`));
        this._saveButton = element(by.css('button[e2e="dashboard-designer-save-button"]'));
        this._filterButton = element(by.css('button[e2e="dashboard-designer-filters-button"]'));
    }

    getSaveButton() {
        return this._saveButton;
    }

    verifySaveButton(){
        expect(this._saveButton.isDisplayed).toBeTruthy();
    }
    
    clickOnAddWidgetButton() {
        commonFunctions.clickOnElement(this._addWidgetButton);
    }

    clickonSaveButton() {
        commonFunctions.clickOnElement(this._saveButton);
    }

    clickonFilterButton() {
        commonFunctions.clickOnElement(this._filterButton);
    }
}

module.exports = CreateNewDashboard;