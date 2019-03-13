'use strict' 

const commonFunctions = require('../utils/commonFunctions');
const SelectedItem = require('../components/SelectedItem');

class ChooseCategory extends SelectedItem{
    constructor(){
        super();
        this._category = (name) =>  element(by.xpath(`//span[contains(text(),"${name}")]`));
        this._subCategory = (name) => element(by.xpath(`//span[contains(text(),"${name}")]`));
    }

    clickOnCategory(categoryName) {
        commonFunctions.clickOnElement(this._category(categoryName));
    }

    clickOnSubCategory(subCategoryName) {
        commonFunctions.clickOnElement(this._subCategory(subCategoryName));
    }
}

module.exports = ChooseCategory;