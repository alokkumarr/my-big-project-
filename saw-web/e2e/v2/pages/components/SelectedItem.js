'use strict'

const commonFunctions = require('../utils/commonFunctions');
const SaveDashboardDialog = require('../components/SaveDashboardDialog');

class SelectedItem extends SaveDashboardDialog{
    constructor(){
        super();
        this._addAnalysisById= (id) =>  element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`));
        this._removeAnalysisById = (id) =>  element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`));
        this._kpiColumnByName = name => element(by.xpath(`//button[@e2e="dashboard-add-kpi-column-${name}"]`));     
        this._kpiName = element(by.css('input[e2e="dashboard-add-kpi-name-input"]'));
        this._dateFieldSelect = element(by.css('[e2e="dashboard-add-kpi-date-column-select"]'));
        this._dateOptionValue = name =>element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`));
        this._datePreSelect = element(by.css('[e2e="dashboard-add-kpi-date-preset-select"]'));
        this._datePreselectValue = name => element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`));
        this._aggregationSelect = element(by.css('[e2e="dashboard-add-kpi-aggregation-select"]'));
        this._aggregationSelectValue = name => element(by.xpath(`//span[contains(text(),"${name}") and @class="mat-option-text"]`));
        this._secondaryAggregateByName = name => element(by.xpath(`//*[@e2e="dashboard-add-kpi-secondary-aggregate-${name}"]`));
        this._backgroundColorByName = name => element(by.xpath(`//*[@e2e="dashboard-add-kpi-color-${name}"]`));
        this._applyKPIButton = element(by.css('button[e2e="dashboard-add-kpi-apply-button"]')); 
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

    clickOnKpiColumnByName(name){
        commonFunctions.clickOnElement(this._kpiColumnByName(name));
    }

    fillKPINameDetails(text) {
        commonFunctions.fillInput(this._kpiName, text);
    }

    clickOnDateFieldSelect(){
        commonFunctions.clickOnElement(this._dateFieldSelect);
    }

    clickOnDateOPtionValue(dateOption){
        commonFunctions.clickOnElement(this._dateOptionValue(dateOption));
    }

    clickOnDatePreSelect(){
        commonFunctions.clickOnElement(this._datePreSelect);
    }

    clickOnDatePreSelectValue(name){
        commonFunctions.clickOnElement(this._datePreselectValue(name));
    }

    clickOnAggregationSelect(){
        commonFunctions.clickOnElement(this._aggregationSelect);
    }

    clickOnAggregationSelectValue(value) {
        commonFunctions.clickOnElement(this._aggregationSelectValue(value));
    }

    addSecondryAggregations(kpiInfo){
        kpiInfo.secondaryAggregations.forEach(secondaryAggregation => {
            if (secondaryAggregation.toLowerCase() !== kpiInfo.primaryAggregation.toLowerCase()) {
                commonFunctions.clickOnElement(this._secondaryAggregateByName(secondaryAggregation));
            }
        });
    }

    clickOnBackgroundColorByName(color){
        commonFunctions.clickOnElement(this._backgroundColorByName(color));
    }

    clickOnApplyKPIButton(){
        commonFunctions.clickOnElement(this._applyKPIButton);
    }

}

module.exports = SelectedItem;