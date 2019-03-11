'use strict'

const commonFunctions = require('../utils/commonFunctions');
const ConfirmDeleteDialog = require('../components/ConfirmDeleteDialog');

class SaveDashboardDialog extends ConfirmDeleteDialog{
    constructor(){
        super();
        this._dashboardName = element(by.css('input[e2e="dashboard-save-name-input"]'));
        this._dashboardDesc = element(by.css('textarea[e2e="dashboard-save-description-input"]'));
        this._categorySelect = element(by.css('[e2e="dashboard-save-category-select"]'));
        this._subCategorySelect =  (name) =>  element(by.xpath(`//span[@class="mat-option-text"and contains(text(),"${name}")]`));
        this._saveDialogButton = element(by.css(`[e2e="dashboard-save-save-button"]`));
        this._cancelDialogButton = element(by.css(`[e2e="dashboard-save-cancel-button"]`));        
    }

    setDashboardName(text) {
        commonFunctions.fillInput(this._dashboardName, text);
    }

    setDashboardDescription(text) {
        commonFunctions.fillInput(this._dashboardDesc, text);
    }

    clickOnCategorySelect() {
        commonFunctions.clickOnElement(this._categorySelect);
    }

    clickOnSubCategorySelect(name) {
        commonFunctions.clickOnElement(this._subCategorySelect(name));
    }

    clickOnSaveDialogButton() {
        commonFunctions.clickOnElement(this._saveDialogButton);
    }

    clickOnCancelDialogButton() {
        commonFunctions.clickOnElement(this._cancelDialogButton);
    }
}

module.exports = SaveDashboardDialog;