'use strict';

const commonFunctions = require('./utils/commonFunctions');
const ChooseWidget = require('./components/ChooseWidget');

class DashboardDesigner extends ChooseWidget{
    constructor() {
        super();
        this._addWidgetButton = element(by.css(`[e2e='dashboard-add-widget-button']`));
        this._saveButton = element(by.css('button[e2e="dashboard-designer-save-button"]'));
        this._filterButton = element(by.css('button[e2e="dashboard-designer-filters-button"]'));
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

module.exports = DashboardDesigner;