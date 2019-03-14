'use strict';

const commonFunctions = require('../utils/commonFunctions');
const ChooseCategory = require('../components/ChooseCategory');

class ChooseWidget extends ChooseCategory{
    constructor(){
        super();
        this._existingAnalysisLink= element(by.css('button[e2e="dashboard-add-widget-type-analysis"]'));
        this._snapshotKPILink= element(by.css('button[e2e="dashboard-add-widget-type-kpi"]'));
    }
    
    clickOnExistingAnalysisLink() {
        commonFunctions.clickOnElement(this._existingAnalysisLink);
    }

    clickOnSnapshotKPILink() {
        commonFunctions.clickOnElement(this._snapshotKPILink);
    }

}

module.exports = ChooseWidget;