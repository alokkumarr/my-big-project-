'use strict';

const commonFunctions = require('./utils/commonFunctions');


class ObservePage {
    constructor(){
        this._addDashboardButton = element(by.css(`[e2e='dashboard-new-dashboard-button']`));
        this._manualRefreshButton = element(by.css(`[e2e='dashboard-manual-refresh-button']`));
        this._deleteDashboardButton = element(by.css(`[e2e='dashboard-delete-dashboard-button']`));
        this._downloadDashboardButton = element(by.css(`[e2e='dashboard-download-dashboard-button']`));
        this._editDashboardButton = element(by.css(`[e2e='dashboard-edit-dashboard-button']`));
        this._openGlobalFilterButton = element(by.css(`[e2e='dashboard-open-global-filters-button']`));
        this._addWidgetButton = element(by.css(`[e2e='dashboard-add-widget-button']`));
        this._existingAnalysisLink= element(by.css('button[e2e="dashboard-add-widget-type-analysis"]'));
        this._category = (name) => { return element(by.xpath(`//span[contains(text(),"${name}")]`))};
        this._subCategory = (name) => {return element(by.xpath(`//span[contains(text(),"${name}")]`))};
        this._addAnalysisById= (id) => { return element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`))};
        this._removeAnalysisById = (id) => { return element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`))};
        this._saveButton = element(by.css('button[e2e="dashboard-designer-save-button"]'));
        this._dashboardTitle = name => { return  element(by.xpath(`//span[contains(text(),"${name}")]`))};
        this._addedAnalysisByName = name => { return element(by.xpath(`//h1[text()="${name}"]`))};
        this._dashboardAction = action => { return  element(by.xpath(`//span[contains(text(),"${action}")]`))};
        this._dashboardName = element(by.css('input[e2e="dashboard-save-name-input"]'));
        this._dashboardDesc = element(by.css('textarea[e2e="dashboard-save-description-input"]'));
        this._categorySelect = element(by.css('[e2e="dashboard-save-category-select"]'));
        this._subCategorySelect =  (name) => { return element(by.xpath(`//span[@class="mat-option-text"and contains(text(),"${name}")]`))};
        this._saveDialogBtn = element(by.css(`[e2e="dashboard-save-save-button"]`));
        this._dashboardConfirmDeleteButton = element(by.css('[e2e="dashboard-confirm-dialog-confirm"]'));
    }

    clickOnSaveDialogButton() {
        commonFunctions.clickOnElement(this._saveDialogBtn);
    }

    clickOnSubCategorySelect(name) {
        commonFunctions.clickOnElement(this._subCategorySelect(name));
    }
    clickOnCategorySelect() {
        commonFunctions.clickOnElement(this._categorySelect);
    }

    getDashboardDescElement() {
        return this._dashboardDesc;
    }

    getDashboardNameElement(){
        return this._dashboardName;
    }

    getDashboardAction(action){
        return this._dashboardAction(action);
    }

    getAddedAnalysisName(name) {
        return this._addedAnalysisByName(name)
    }

    getDashboardTitle(title) {
        return this._dashboardTitle(title);
    }

    clickonSaveButton() {
        commonFunctions.clickOnElement(this._saveButton);
    }

    getSaveButton() {
        return this._saveButton;
    }

    clickonAddAnalysisIdButton(id) {
        commonFunctions.clickOnElement(this._addAnalysisById(id));
    }

    getRemoveAnalysisByIdElement(id) {
        return this._removeAnalysisById(id);
    }

    clickOnCategory(categoryName) {
        commonFunctions.clickOnElement(this._category(categoryName));
    }

    clickOnSubCategory(subCategoryName) {
        commonFunctions.clickOnElement(this._category(subCategoryName));
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

    clickOnDashboardConfirmDeleteButton() {
        commonFunctions.clickOnElement(this._dashboardConfirmDeleteButton);        
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

    clickOnAddWidgetButton() {
        commonFunctions.clickOnElement(this._addWidgetButton);
    }

    clickOnExistingAnalysisLink() {
        commonFunctions.clickOnElement(this._existingAnalysisLink);
    }
}
module.exports = ObservePage;
