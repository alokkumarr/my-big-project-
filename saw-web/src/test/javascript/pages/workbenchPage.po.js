var appRoot = require('app-root-path');
const {doMdSelectOption, getMdSelectOptions} = require(appRoot + '/src/test/javascript/helpers/utils');
const commonFunctions = require(appRoot + '/src/test/javascript/helpers/commonFunctions.js');
const protractorConf = require(appRoot + '/conf/protractor.conf');
const webpackHelper = require(appRoot + '/conf/webpack.helper');
const designModePage = require(appRoot + '/src/test/javascript/pages/designModePage.po');
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
        previewFile: element(by.css('span.preview-icon[e2e="preview-file"]')),
        closeRawPreviewFile: element(by.css('rawpreview-dialog button[e2e="close-raw-preview-dialog-button"]')),
      },
      detailedDataSet: {
          cardViewDataSetElem: element(by.css('a[e2e="data-set-card"]')),
          listViewDataSetElem: element(by.css('div.grid-container a[e2e="data-set-grid-view-mode"]')),

          dataSetSummaryPreview: element(by.css('div.data-summary-view[e2e="data-set-summary-preview"]')),
          dataSetDetailPreview: element(by.css('div.data-cont[e2e="data-set-detial-preview"]')),
      },
      dataSetActionElement: {
          dataSetAction: element(by.css('dataset-actions')),
          menuAction: element(by.css('button.mat-menu-item[role="menuitem"]')),
          firstWindowStepperFwd: element(by.css('mat-step[e2e="create-dataset-first-window"] button[e2e="first-window-stepperFwd"]')),
          firstWindowStepperCancel: element(by.css('mat-step[e2e="create-dataset-first-window"] button[e2e="first-window-stepperCancel"]')),
          
          secondWindowStepperCancel: element(by.css('mat-step[e2e="create-dataset-second-window"] button[e2e="second-window-stepperCancel"]')),
          secondWindowStepperFwd: element(by.css('mat-step[e2e="create-dataset-second-window"] button[e2e="second-window-stepperFwd"]')),
          secondWindowStepperPrev: element(by.css('mat-step[e2e="create-dataset-second-window"] button[e2e="second-window-stepperPrev"]')),
          
          thirdWindowStepperCancel: element(by.css('mat-step[e2e="create-dataset-third-window"] button[e2e="third-window-stepperCancel"]')),
          thirdWindowStepperFwd: element(by.css('mat-step[e2e="create-dataset-third-window"] button[e2e="third-window-stepperFwd"]')),
          thirdWindowStepperPrev: element(by.css('mat-step[e2e="create-dataset-third-window"] button[e2e="third-window-stepperPrev"]')),

          fourthWindowStepperCancel: element(by.css('mat-step[e2e="create-dataset-fourth-window"] button[e2e="fourth-window-stepperCancel"]')),
          fourthWindowStepperAdd: element(by.css('mat-step[e2e="create-dataset-fourth-window"] button[e2e="fourth-window-stepperAdd"]')),
          fourthWindowStepperPrev: element(by.css('mat-step[e2e="create-dataset-fourth-window"] button[e2e="fourth-window-stepperPrev"]')),

          rawPreviewData: element(by.css('mat-tab[e2e="raw-data-preview"]')),
      },
      newDialogue: {
          dialogue: element(by.css('createfolder-dialog')),
          getInputOfDialogue: name => element(by.css(`createfolder-dialog form.new-folder button[e2e="new-folder-name-${name}"]`)),
          submitFolderNameElem: name =>  element(by.css(`button[e2e="submit-folder-name-${name}"]`))
      },
    workbenchElems : {
        view: element(by.css('div[e2e="view-mode-group"] mat-button-toggle-group')),
        data: element(by.css('div[e2e="data-mode-group"] mat-button-toggle-group')),
        refreshBtn: element(by.css('div.action-buttons button.mat-icon-button[ng-reflect-message="Refresh"]')),
        listView: element(by.css('div.view-mode mat-button-toggle[e2e="list-view"]')),
        cardView: element(by.css('div.view-mode mat-button-toggle[e2e="card-view"]')),
        setsView: element(by.css('div.data-mode mat-button-toggle[e2e="data-sets-view"]')),
        podsView: element(by.css('div.data-mode mat-button-toggle[e2e="data-pods-view"]')),
        addDataSetBtn: element(by.css('button[e2e="add-new-data-sets"]')),
        nextStepperBtn: element(by.css('button.stepperBtn[matsteppernext]')),
        prevStepperBtn: element(by.css('button.stepperBtn[matstepperprevious]')),
        fieldSeparatorElement: element(by.css('div.form-div > form input[formcontrolname="fieldSeperatorControl"]')),
        dataSetName: element(by.css('input[e2e="dataset-name"]')),
        dataSetDescription: element(by.css('input[e2e="dataset-desc"]')),
        fileUploadElem: element(by.css('button[e2e="upload-selected-file"]')),
        createNewFolderElem: element(by.css('button[e2e="create-new-folder"]')),       
        fileInputElem: element(by.css('input[e2e="uploaded-file" type="file"]')),                
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
        element(by.css('input[e2e="uploaded-file"]')).sendKeys(absolutePath);    
        browser.actions().sendKeys(protractor.Key.ENTER).perform();
        // element(by.css('input[e2e="upload-selected-file"]')).click();
    }
}; 