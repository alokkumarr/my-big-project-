'use strict'

const commonFunctions = require('../../pages/utils/commonFunctions');
const logger = require('../../conf/logger')(__filename);

class GlobalFilters{
    constructor(){
        this._stringFilter = element(by.css(`input[e2e="dashboard-global-filter-string-input"]`));
        this._stringFilterValue = value => element(by.xpath(`//mat-option[contains(text(),"${value}")]`));
        this._dateFilterPreset = element(by.css('[e2e="filter-date-preset"]'));
        this._dateFilterPresetValue = value => element(by.xpath(`//mat-option[contains(text(),"${value}")]`));
        this._applyFilter = element(by.css(`button[e2e="dashboard-global-filters-apply-button"]`));
        this._numberSliderLow = element(
          by.xpath(
            `//*[@e2e="dashboard-global-filter-number-slider"]/descendant::*[contains(@class,"noUi-handle-lower")]`
          )
        );
        this._selectedPresetValue = value =>element(by.xpath(`//*[contains(text(),"${value}")]`));
        this._cancelFilter = element(by.css('[e2e="dashboard-global-filters-cancel-button"]'));
        this._numberSlider = element(by.css('[e2e="dashboard-global-filter-number-slider"]'));
    }

    verifyAppliedGlobalFilters(dashboardGlobalFilters){
        try{
            dashboardGlobalFilters.forEach( currentFilter => {
                if (currentFilter.name.toLowerCase() === 'string') {
                    browser.sleep(2000);
                    expect(this._stringFilter.getAttribute('value')).toBe(currentFilter.value);
                } else if (currentFilter.name.toLowerCase() === 'date') {
                    browser.sleep(2000);
                    expect(this._selectedPresetValue(currentFilter.preset).getText()).toContain(currentFilter.preset);
                } else if (currentFilter.name.toLowerCase() === 'long' || currentFilter.name.toLowerCase() === 'integer' || currentFilter.name.toLowerCase() === 'float' || currentFilter.name.toLowerCase() === 'double') {
                    browser.sleep(2000);
                    expect(this._numberSliderLow.getAttribute('aria-valuenow')).toBeGreaterThan(0.0);
                }
            });
            commonFunctions.waitFor.elementToBeVisible(this._cancelFilter)
            commonFunctions.clickOnElement(this._cancelFilter);
        } catch(e){
            logger.error(e);
        }
    }

    applyAndVerifyGlobalFilters(dashboardGlobalFilters){
        try{
            dashboardGlobalFilters.forEach(currentFilter => {
                if (currentFilter.name.toLowerCase() === 'string') {
                    browser.sleep(2000);
                    commonFunctions.fillInput(this._stringFilter, currentFilter.value);
                    commonFunctions.clickOnElement(this._stringFilterValue(currentFilter.value));
                } else if (currentFilter.name.toLowerCase() === 'date') {
                    browser.sleep(2000);
                    commonFunctions.clickOnElement(this._dateFilterPreset);
                    commonFunctions.clickOnElement(this._dateFilterPresetValue(currentFilter.preset));
                } else if (currentFilter.name.toLowerCase() === 'long' || currentFilter.name.toLowerCase() === 'integer' || currentFilter.name.toLowerCase() === 'float' || currentFilter.name.toLowerCase() === 'double') {
                    browser.sleep(2000);
                    commonFunctions.waitFor.elementToBeVisible(this._numberSlider);
                    commonFunctions.slideHorizontally(this._numberSliderLow, currentFilter.value);
                }
            });
            commonFunctions.clickOnElement(this._applyFilter);
        } catch(err) {
            logger.error(err);
        }
    }

}

module.exports = GlobalFilters;
