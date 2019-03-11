'use strict';

const commonFunctions = require('../utils/commonFunctions');
const ChooseCategory = require('../components/ChooseCategory');

class ChooseWidget extends ChooseCategory{
    constructor(){
        super();
        this._existingAnalysisLink= element(by.css('button[e2e="dashboard-add-widget-type-analysis"]'));
    }
    
    clickOnExistingAnalysisLink() {
        commonFunctions.clickOnElement(this._existingAnalysisLink);
    }
}

module.exports = ChooseWidget;