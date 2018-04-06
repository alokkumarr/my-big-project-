const {doMdSelectOption, getMdSelectOptions} = require('../helpers/utils');
const commonFunctions = require('../helpers/commonFunctions.js');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const webpackHelper = require('../../../../conf/webpack.helper');
const designModePage = require('./designModePage.po');
/* const getCards = name => element.all(by.css('md-card[e2e="analysis-card"]')).filter(elem => {
  return elem.element(by.cssContainingText('a[e2e="analysis-name"]', name));
}); */
const getSelectedFileName = (index) => {`button[e2e="actions-menu-selector-${action}"]`
   return element(by.css('div.dx-datagrid-content table tbody tr'));
};

module.exports = {

    fileElements: {
        maskField: element(by.css('mat-form-field input[matinput]')),
        fileName: element(by.css('div.gridContainer dx-data-grid#gridContainer div.dx-datagrid div.dx-datagrid-rowsview table.dx-datagrid-table[role="grid"] > tbody > tr')),
      },
      dataSetActionElement: {
          dataSetAction: element(by.css('dataset-actions')),
          menuAction: element(by.css('button.mat-menu-item[role="menuitem"]'))
      },
    workbenchElems : {
        view: element(by.css('div.view-mode mat-button-toggle-group')),
        data: element(by.css('div.data-mode mat-button-toggle-group')),
        refreshBtn: element(by.css('div.action-buttons button.mat-icon-button[ng-reflect-message="Refresh"]')),
        listView: element(by.css('div.view-mode mat-button-toggle-group mat-button-toggle[value="list"]')),
        cardView: element(by.css('div.view-mode mat-button-toggle-group mat-button-toggle[value="card"]')),
        setsView: element(by.css('div.data-mode mat-button-toggle-group mat-button-toggle[value="sets"]')),
        podsView: element(by.css('div.data-mode mat-button-toggle-group mat-button-toggle[value="pods"]')),
        addDataSetBtn: element(by.css('div.action-buttons button.mat-raised-button')),
        nextStepperBtn: element(by.css('button.stepperBtn[matsteppernext]')),
        prevStepperBtn: element(by.css('button.stepperBtn[matstepperprevious]')),
        fieldSeparatorElement: element(by.css('div.form-div > form input[formcontrolname="fieldSeperatorControl"]')),
        dataSetName: element(by.css('div.mat-input-infix input[formcontrolname="nameControl"]')),
        dataSetDescription: element(by.css('div.mat-input-infix input[formcontrolname="descControl"]')),
        fileUploadElem: element(by.css('select-rawdata div.select-view mat-card mat-card-header.headerGradient button[for="fileUpload"]')),
    },
    validateCardViewMode() {
        expect(this.workbenchElems.view.getAttribute('ng-reflect-value')).toEqual('card');
    },
    validateListViewMode() {
        expect(this.workbenchElems.view.getAttribute('ng-reflect-value')).toEqual('list');
    },
    validateSetViewMode() {
        expect(this.workbenchElems.data.getAttribute('ng-reflect-value')).toEqual('sets');
    },
    validatePodsViewMode() {
        expect(this.workbenchElems.data.getAttribute('ng-reflect-value')).toEqual('pods');
    },
    uploadFile(absolutePath, elem) {
        element(by.css(elem)).sendKeys(absolutePath);    
        browser.actions().sendKeys(protractor.Key.ENTER).perform();
    }
}; 